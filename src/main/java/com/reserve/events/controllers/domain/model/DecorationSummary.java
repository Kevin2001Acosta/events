package com.reserve.events.controllers.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecorationSummary {
    @NotBlank(message = "El ID de la decoración es obligatorio si se incluye una decoración")
    @Schema(description = "ID único de la decoración", example = "dec_789")
    private String id;

    @NotBlank(message = "Los artículos de decoración son obligatorios si se incluye una decoración")
    @Schema(description = "Artículos incluidos en la decoración", example = "Globos, manteles, flores")
    private String articles;

    @NotNull(message = "El costo de la decoración es obligatorio si se incluye una decoración")
    @Schema(description = "Costo total de la decoración", example = "200.0")
    private Double cost;
}
