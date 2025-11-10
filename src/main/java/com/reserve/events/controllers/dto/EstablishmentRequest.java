package com.reserve.events.controllers.dto;

import com.reserve.events.controllers.domain.model.EstablishmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Petición para crear o actualizar un establecimiento")
public class EstablishmentRequest {

    @NotBlank
    @Schema(description = "Nombre del establecimiento", example = "Salón Primavera")
    private String name;

    @NotBlank
    @Schema(description = "Dirección", example = "Calle 45 #12-34")
    private String address;

    @NotBlank
    @Schema(description = "Teléfono", example = "3001234567")
    private String phone;

    @NotBlank
    @Schema(description = "Ciudad", example = "Bogotá")
    private String city;

    @NotNull
    @Schema(description = "Capacidad máxima", example = "200")
    private Integer capacity;

    @NotNull
    @Schema(description = "Tipo de establecimiento", example = "MEDIUM")
    private EstablishmentType type;

    @NotNull
    @Schema(description = "Costo de alquiler", example = "1500.0")
    private Double cost;

    @NotBlank
    @Schema(description = "URL de imagen", example = "https://example.com/image.jpg")
    private String imageUrl;
}