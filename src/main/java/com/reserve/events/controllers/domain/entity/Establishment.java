package com.reserve.events.controllers.domain.entity;

import com.reserve.events.controllers.domain.entity.EstablishmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Establecimientos")
@Schema(description = "Entidad que representa un establecimiento")
public class Establishment {

    @Id
    private String id;

    @NotBlank(message = "El nombre del establecimiento es obligatorio")
    @Schema(description = "Nombre del establecimiento", example = "Salón de eventos Primavera")
    private String name;

    @NotBlank(message = "La dirección del establecimiento es obligatoria")
    @Schema(description = "Dirección del establecimiento", example = "Calle 123 #45-67")
    private String address;

    @NotBlank(message = "El teléfono del establecimiento es obligatorio")
    @Schema(description = "Teléfono de contacto del establecimiento", example = "3001234567")
    private String phone;

    @NotBlank(message = "La ciudad del establecimiento es obligatoria")
    @Schema(description = "Ciudad donde se encuentra el establecimiento", example = "Bogotá")
    private String city;

    @NotNull(message = "La capacidad del establecimiento es obligatoria")
    @Schema(description = "Capacidad máxima del establecimiento", example = "200")
    private Integer capacity;

    @NotNull(message = "El tipo de establecimiento es obligatorio")
    @Schema(description = "Tipo de establecimiento", example = "SMALL", allowableValues = {"SMALL", "MEDIUM", "LARGE"})
    private EstablishmentType type;

    @NotNull(message = "El costo del establecimiento es obligatorio")
    @Schema(description = "Costo de alquiler del establecimiento", example = "1500.0")
    private Double cost;

    @NotBlank(message = "La URL de la imagen del establecimiento es obligatoria")
    @Schema(description = "URL de la imagen del establecimiento", example = "https://example.com/establishment-image.jpg")
    private String imageUrl;
}
