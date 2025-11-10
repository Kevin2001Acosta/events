package com.reserve.events.controllers.domain.model;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstablishmentSummary {

    @NotBlank(message = "El id del establecimiento es obligatorio")
    @Schema(description = "Identificador único del establecimiento", example = "est_123")
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "El nombre del establecimiento es obligatorio")
    @Schema(description = "Nombre del establecimiento", example = "Salón de eventos Primavera")
    private String name;

    @NotBlank(message = "La dirección del establecimiento es obligatoria")
    @Schema(description = "Dirección del establecimiento", example = "Calle 123 #45-67")
    private String address;

    @NotBlank(message = "El teléfono del establecimiento es obligatorio")
    @Schema(description = "Teléfono de contacto del establecimiento", example = "3001234567")
    private String phone;

    @NotBlank(message = "El costo del establecimiento es obligatorio")
    @Schema(description = "Costo total del establecimiento", example = "1500.0")
    private Double totalCost;
}
