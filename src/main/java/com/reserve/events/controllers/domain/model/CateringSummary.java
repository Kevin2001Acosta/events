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
@Schema(description = "Resumen de un servicio de caterin para ser incrustado en otras entidades")
public class CateringSummary {

    @NotBlank(message = "El ID del catering es obligatorio si se incluye un catering")
    @Schema(description = "ID único del servicio de catering", example = "cat_123")
    private String id;

    @NotNull(message = "El tipo de menu es obligatorio si se incluye un catering")
    @Schema(description = "Tipo de menu del servicio de comida", example = "VEGETARIANO", allowableValues = {"BUFFET", "VEGETARIANO", "INFANTIL", "GOURMET"})
    private MenuCatering menuType;

    @NotBlank(message = "La descripción del catering es obligatoria si se incluye un catering")
    @Schema(description = "Descripción del servicio de catering", example = "Catering para 50 personas")
    private String description;

    @NotNull(message = "El número de platos servidos es obligatorio si se incluye un catering")
    @Schema(description = "Número de platos servidos", example = "50")
    private Integer numberDish;

    @NotNull(message = "El costo total del catering es obligatorio si se incluye un catering")
    @Schema(description = "Costo total del servicio de catering", example = "500.0")
    private Double totalCost;
}
