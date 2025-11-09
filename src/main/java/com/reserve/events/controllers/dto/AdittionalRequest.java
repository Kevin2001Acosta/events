package com.reserve.events.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la creación y actualización de un servicio adicional
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para crear o actualizar un servicio adicional")
public class AdittionalRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(
            description = "Nombre del servicio adicional",
            example = "Fotógrafo profesional",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Schema(
            description = "Descripción del servicio adicional",
            example = "Servicio de fotografía profesional para eventos",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String description;

    @NotNull(message = "El costo es obligatorio")
    @Schema(
            description = "Costo del servicio adicional",
            example = "300000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private double cost;
}
