package com.reserve.events.controllers.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdittionalSummary {

    @NotBlank(message = "El ID del servicio adicional es obligatorio si se incluye un servicio adicional")
    @Schema(description = "ID único del servicio adicional", example = "add_456")
    private String id;

    @NotBlank(message = "El nombre del servicio adicional es obligatorio si se incluye un servicio adicional")
    @Schema(description = "Nombre del servicio adicional", example = "Fotografía profesional")
    private String name;

    @NotBlank(message = "La descripción del servicio adicional es obligatoria si se incluye un servicio adicional")
    @Schema(description = "Descripción del servicio adicional", example = "Fotografía profesional")
    private String description;

    @NotNull(message = "La cantidad comprada del servicio adicional es obligatoria si se incluye un servicio adicional")
    @Schema(description = "Cantidad comprada del servicio adicional", example = "300.0")
    private Integer quantity;

    @NotNull(message = "El costo total del servicio adicional es obligatorio si se incluye un servicio adicional")
    @Schema(description = "Costo total del servicio adicional", example = "300.0")
    private Double totalCost;
}
