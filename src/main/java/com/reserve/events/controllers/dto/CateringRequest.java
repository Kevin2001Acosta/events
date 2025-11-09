package com.reserve.events.controllers.dto;

import com.reserve.events.controllers.domain.model.MenuCatering;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la creación y actualización de un servicio de Caterin
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para crear o actualizar un servicio de Caterin")
public class CateringRequest {

    @NotNull(message = "El tipo de menu es obligatorio")
    @Schema(
            description = "Tipo de menu del servicio de comida",
            example = "VEGETARIANO", allowableValues = {"BUFFET", "VEGETARIANO", "INFANTIL", "GOURMET"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private MenuCatering menuType;

    @NotBlank(message = "La descripción es obligatoria")
    @Schema(
            description = "Descripción del servicio de comida",
            example = "",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String description;

    @NotNull(message = "El costo del plato es obligatorio")
    @Schema(
            description = "El costo por plato requerido por el cliente en el servicio de comida",
            example = "80",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private double costDish;
}
