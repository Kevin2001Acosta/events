package com.reserve.events.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la creación y actualización de un servicio de decoración
 * */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para crear o actualizar un servicio de decoración")
public class DecorationRequest {

    @NotBlank(message = "El tema es obligatorio")
    @Schema(
            description = "Tema de la decoración",
            example = "Fiesta infantil",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String theme;

    @NotBlank(message = "Los artículos son obligatorios")
    @Schema(
            description = "Artículos de la decoración",
            example = "Globos, guirnaldas, mesa de dulces",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String articles;

    @NotNull(message = "El costo es obligatorio")
    @Schema(
            description = "Costo de la decoración en pesos colombianos",
            example = "100000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private double cost;
}
