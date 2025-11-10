package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Payment;
import com.reserve.events.controllers.domain.repository.PaymentRepository;
import com.reserve.events.controllers.dto.PaymentRequest;
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
     * Actualiza el estado o la información del pago.
     */
    @Transactional
    public PaymentResponse updatePayment(String id, PaymentRequest request) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Pago no encontrado con ID: " + id));

        existingPayment.setDescription(request.getDescription());
        existingPayment.setStatus(request.getStatus());
        existingPayment.setTotalCost(request.getTotalCost());
        existingPayment.setClient(request.getClient());
        existingPayment.setReserve(request.getReserve());
        existingPayment.setCoveredServices(request.getCoveredServices());

        Payment updatedPayment = paymentRepository.save(existingPayment);
        log.info("Pago actualizado con ID: {}", updatedPayment.getId());

        return mapToPaymentResponse(updatedPayment);
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
