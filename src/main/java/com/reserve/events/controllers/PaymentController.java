package com.reserve.events.controllers;

import com.reserve.events.application.PaymentService;
import com.reserve.events.controllers.dto.PaymentRequest;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/payments")
@Tag(name = "Pagos", description = "API para la gestión de pagos de reservas")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 🔹 Crear un nuevo pago
    @PostMapping
    @Operation(summary = "Crear un nuevo pago")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pago creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "409", description = "Ya existe un pago con el mismo identificador")
    })
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.createPayment(paymentRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 🔹 Actualizar un pago existente
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un pago existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado")
    })
    public ResponseEntity<PaymentResponse> updatePayment(
            @PathVariable String id,
            @Valid @RequestBody PaymentRequest paymentRequest) {
        PaymentResponse response = paymentService.updatePayment(id, paymentRequest);
        return ResponseEntity.ok(response);
    }

    // 🔹 Obtener lista de todos los pagos
    @GetMapping
    @Operation(summary = "Obtener lista de todos los pagos")
    @ApiResponse(responseCode = "200", description = "Lista de pagos obtenida exitosamente")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
}
