package com.reserve.events.controllers.dto;

import com.reserve.events.controllers.domain.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar un pago existente.
 * Solo permite modificar el status y la descripción.
 * Los demás datos del pago se mantienen intactos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para actualizar un pago (solo status y descripción)")
public class PaymentUpdateRequest {

    @Schema(
        description = "Nuevo estado del pago. Si no se envía, se mantiene el actual.",
        example = "COMPLETADO",
        allowableValues = {"PENDIENTE", "COMPLETADO", "CANCELADO"}
    )
    private PaymentStatus status;

    @Size(min = 5, max = 500, message = "La descripción debe tener entre 5 y 500 caracteres")
    @Schema(
        description = "Nueva descripción del pago. Si no se envía, se mantiene la actual.",
        example = "Pago completado el 9 de diciembre de 2025"
    )
    private String description;
}

