package com.reserve.events.controllers.dto;

import com.reserve.events.controllers.domain.entity.Payment;
import com.reserve.events.controllers.domain.model.EstablishmentSummary;
import com.reserve.events.controllers.domain.model.PaymentStatus;
import com.reserve.events.controllers.domain.model.UserSummary;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para la creación y actualización de pagos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para crear o actualizar un pago")
public class PaymentRequest {

    @NotBlank(message = "La descripción del pago es obligatoria")
    @Size(min = 5, max = 500, message = "La descripción debe tener entre 5 y 500 caracteres")
    @Schema(
        description = "Descripción del pago",
        example = "Pago correspondiente al servicio de entretenimiento y catering para la reserva del 15 de diciembre."
    )
    private String description;

    @NotNull(message = "El estado del pago es obligatorio")
    @Schema(
        description = "Estado actual del pago",
        example = "PENDIENTE",
        allowableValues = {"PENDIENTE", "COMPLETADO", "CANCELADO"}
    )
    private PaymentStatus status;

    @NotNull(message = "El costo total es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El costo total debe ser mayor que 0")
    @Schema(
        description = "Costo total del pago en pesos colombianos",
        example = "250000.0"
    )
    private Double totalCost;

    @NotNull(message = "La información del cliente es obligatoria")
    @Schema(
        description = "Información mínima del cliente asociada al pago"
    )
    private UserSummary client;

    @NotNull(message = "La información de la reserva es obligatoria")
    @Schema(
        description = "Información mínima de la reserva asociada al pago"
    )
    private Payment.ReserveInfo reserve;

    @NotNull(message = "Debe incluir los servicios cubiertos por el pago")
    @Schema(
        description = "Estructura detallada de los servicios cubiertos por el pago"
    )
    private Payment.CoveredServices coveredServices;

    @NotNull(message = "La información del establecimiento es obligatoria")
    @Schema(
        description = "Información mínima del establecimiento asociado al pago"
    )
    private EstablishmentSummary establishment;
}
