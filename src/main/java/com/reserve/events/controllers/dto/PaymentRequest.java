package com.reserve.events.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

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
        example = "Pago correspondiente al servicio de entretenimiento y catering para la reserva del 15 de diciembre.",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String description;

    @NotBlank(message = "El estado del pago es obligatorio")
    @Pattern(regexp = "^(PENDIENTE|COMPLETADO|CANCELADO)$",
            message = "El estado debe ser PENDIENTE, COMPLETADO o CANCELADO")
    @Schema(
        description = "Estado actual del pago",
        example = "COMPLETADO",
        allowableValues = {"PENDIENTE", "COMPLETADO", "CANCELADO"},
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String status;

    @NotNull(message = "El costo total es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El costo total debe ser mayor que 0")
    @Schema(
        description = "Costo total del pago en pesos colombianos",
        example = "250000.0",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Double totalCost;

    @NotBlank(message = "El identificador del cliente es obligatorio")
    @Schema(
        description = "ID del cliente asociado al pago",
        example = "client_12345",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String clientId;

    @NotBlank(message = "El identificador de la reserva es obligatorio")
    @Schema(
        description = "ID de la reserva asociada al pago",
        example = "reserve_98765",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String reserveId;

    @NotEmpty(message = "Debe incluir al menos un servicio cubierto en el pago")
    @Schema(
        description = "Conjunto de IDs de los servicios cubiertos por el pago",
        example = "[\"entertainment_001\", \"catering_002\"]",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Set<@NotBlank(message = "El ID del servicio no puede estar vacío") String> coveredServices;
}
