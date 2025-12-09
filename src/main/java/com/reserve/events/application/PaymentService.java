package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Payment;
import com.reserve.events.controllers.domain.repository.PaymentRepository;
import com.reserve.events.controllers.dto.PaymentRequest;
import com.reserve.events.controllers.dto.PaymentUpdateRequest;
import com.reserve.events.controllers.response.PaymentResponse;
import com.reserve.events.controllers.exception.PaymentNotFoundException;
import com.reserve.events.controllers.exception.ServiceAlreadyExistsException;
import com.reserve.events.controllers.domain.model.PaymentStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final EmailService emailService;
    private final PaymentPdfService paymentPdfService;

    /**
     * Crea un nuevo pago en estado PENDIENTE.
     */
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        // Verificar si ya existe un pago asociado a la reserva
        if (paymentRepository.existsByReserve_Id(request.getReserve().getId())) {
            throw new ServiceAlreadyExistsException(
                "Ya existe un pago registrado para la reserva con ID: " + request.getReserve().getId());
        }

        // Mapear el request a la entidad Payment
        Payment payment = Payment.builder()
                .description(request.getDescription())
                .status(PaymentStatus.PENDIENTE)
                .totalCost(request.getTotalCost())
                .client(request.getClient())
                .reserve(request.getReserve())
                .coveredServices(request.getCoveredServices())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Pago creado con ID: {}", savedPayment.getId());

        return mapToPaymentResponse(savedPayment);
    }

    /**
     * Obtiene la lista de todos los pagos.
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un pago por su ID.
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(String id) {
        return paymentRepository.findById(id)
                .map(this::mapToPaymentResponse)
                .orElseThrow(() -> new PaymentNotFoundException("Pago no encontrado con ID: " + id));
    }

    /**
     * Actualiza el estado y/o la descripción del pago.
     * Solo se pueden modificar estos campos. Los demás datos permanecen intactos.
     * Si el pago se marca como COMPLETADO, se envía un comprobante PDF por correo.
     */
    @Transactional
    public PaymentResponse updatePayment(String id, PaymentUpdateRequest request) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Pago no encontrado con ID: " + id));

        PaymentStatus previousStatus = existingPayment.getStatus();

        // Solo actualizar los campos si se proporcionan
        if (request.getDescription() != null) {
            existingPayment.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            existingPayment.setStatus(request.getStatus());
        }

        Payment updatedPayment = paymentRepository.save(existingPayment);
        log.info("Pago actualizado con ID: {}", updatedPayment.getId());

        // Si el pago cambió a COMPLETADO, enviar comprobante por correo
        if (request.getStatus() == PaymentStatus.COMPLETADO && previousStatus != PaymentStatus.COMPLETADO) {
            sendPaymentReceiptEmail(updatedPayment);
        }

        return mapToPaymentResponse(updatedPayment);
    }

    /**
     * Genera y envía el comprobante de pago por correo electrónico.
     */
    private void sendPaymentReceiptEmail(Payment payment) {
        try {
            if (payment.getClient() == null || payment.getClient().getEmail() == null) {
                log.warn("No se puede enviar el comprobante: el pago {} no tiene email del cliente", payment.getId());
                return;
            }

            byte[] pdfBytes = paymentPdfService.generatePaymentReceipt(payment);

            String subject = "Comprobante de Pago - Reserva " + payment.getReserve().getId();
            String body = buildEmailBody(payment);
            String fileName = "Comprobante_Pago_" + payment.getId() + ".pdf";

            emailService.sendEmailWithPdfAttachment(
                    payment.getClient().getEmail(),
                    subject,
                    body,
                    pdfBytes,
                    fileName
            );

            log.info("Comprobante de pago enviado a: {}", payment.getClient().getEmail());
        } catch (Exception e) {
            log.error("Error al enviar comprobante de pago {}: {}", payment.getId(), e.getMessage());
            // No lanzamos excepción para no afectar la actualización del pago
        }
    }

    /**
     * Construye el cuerpo del email en formato HTML.
     */
    private String buildEmailBody(Payment payment) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #2980b9;">¡Pago Completado Exitosamente!</h2>
                    <p>Estimado/a <strong>%s</strong>,</p>
                    <p>Le confirmamos que su pago ha sido procesado correctamente.</p>

                    <div style="background-color: #f8f9fa; padding: 15px; border-radius: 5px; margin: 20px 0;">
                        <p><strong>ID del Pago:</strong> %s</p>
                        <p><strong>Total Pagado:</strong> $%,.2f COP</p>
                        <p><strong>Estado:</strong> COMPLETADO</p>
                    </div>

                    <p>Adjuntamos el comprobante de pago en formato PDF para sus registros.</p>

                    <p style="margin-top: 30px;">Gracias por confiar en nuestros servicios.</p>
                    <p style="color: #666; font-size: 12px;">Este es un correo automático, por favor no responda a este mensaje.</p>
                </div>
            </body>
            </html>
            """,
            payment.getClient().getName(),
            payment.getId(),
            payment.getTotalCost()
        );
    }

    /**
     * Mapeo de entidad a DTO.
     */
    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .description(payment.getDescription())
                .status(payment.getStatus())
                .totalCost(payment.getTotalCost())
                .client(payment.getClient())
                .reserve(payment.getReserve())
                .coveredServices(payment.getCoveredServices())
                .build();
    }
}
