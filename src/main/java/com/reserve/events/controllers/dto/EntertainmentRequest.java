package com.reserve.events.controllers.dto;

import com.reserve.events.controllers.domain.model.TypeEntertainment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la creación y actualización de un servicio de entretenimiento
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para crear o actualizar un servicio de entretenimiento")
public class EntertainmentRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(
            description = "Nombre del entretenimiento",
            example = "Payasos",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message = "El tipo es obligatorio")
    @Schema(
            description = "Tipo del entretenimiento",
            example = "ANIMADORES",
            allowableValues = {"ANIMADORES", "MUSICOS", "BAILARINES"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private TypeEntertainment type;

    @NotBlank(message = "La descripción es obligatoria")
    @Schema(
            description = "Descripción del entretenimiento",
             example = "Animación infantil con payasos que realizan juegos interactivos, concursos y dinámicas para niños de todas las edades. Ideal para cumpleaños y eventos familiares.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String description;

    @Min(1)
    @NotNull(message = "La tarifa por hora es obligatoria")
    @Schema(
            description = "Tarifa por hora del entretenimiento en pesos colombianos",
            example = "10000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private double hourlyRate;
}
