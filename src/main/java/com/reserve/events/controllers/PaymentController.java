package com.reserve.events.controllers;

import com.reserve.events.application.PaymentService;
import com.reserve.events.controllers.dto.PaymentRequest;
import com.reserve.events.controllers.dto.PaymentUpdateRequest;
import com.reserve.events.controllers.response.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/payments")
@Tag(name = "Pagos", description = "API para la gestión de pagos de reservas")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Crear un nuevo pago
    @PostMapping
    @Operation(summary = "Crear un nuevo pago")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pago creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Ya existe un pago asociado a la reserva especificada")
    })
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.createPayment(paymentRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Actualizar un pago existente (solo status y descripción)
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un pago existente",
               description = "Solo permite actualizar el status y la descripción del pago. " +
                             "Si el status cambia a COMPLETADO, se envía un comprobante PDF al correo del cliente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ResponseEntity<PaymentResponse> updatePayment(
            @PathVariable String id,
            @Valid @RequestBody PaymentUpdateRequest paymentUpdateRequest) {
        PaymentResponse response = paymentService.updatePayment(id, paymentUpdateRequest);
        return ResponseEntity.ok(response);
    }

    // Obtener lista de todos los pagos
    @GetMapping
    @Operation(summary = "Obtener lista de todos los pagos")
    @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    // Obtener un pago por su ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pago por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago encontrado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable String id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }
}
