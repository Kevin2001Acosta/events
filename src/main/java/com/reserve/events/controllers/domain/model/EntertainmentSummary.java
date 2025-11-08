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
@Schema(description = "Resumen de un servicio de entretenimiento para ser incrustado en otras entidades")
public class EntertainmentSummary {

    @NotBlank(message = "El id del entretenimiento es obligatorio")
    @Schema(description = "Id del entretenimiento", example = "svc_1")
    private String id;

    @NotBlank(message = "El nombre del entretenimiento es obligatorio")
    @Schema(description = "Nombre del entretenimiento", example = "Show de magia")
    private String name;

    @NotNull(message = "El tipo del entretenimiento es obligatorio")
    @Schema(description = "Tipo del entretenimiento", example = "ANIMADORES",  allowableValues = {"ANIMADORES", "MUSICOS", "BAILARINES"})
    private TypeEntertainment type;

    @NotNull(message = "El número de horas contratadas es obligatorio")
    @Schema(description = "Número de horas contratadas", example = "3")
    private Integer hours;

    @NotNull(message = "El costo total del entretenimiento es obligatorio")
    @Schema(description = "Costo total del entretenimiento", example = "500.0")
    private Double totalCost;
}
