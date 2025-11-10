package com.reserve.events.controllers.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.reserve.events.controllers.domain.model.EstablishmentType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Respuesta de la API para un establecimiento")
public class EstablishmentResponse {

    @Schema(description = "Identificador único del establecimiento", example = "est_001")
    private String id;

    @Schema(description = "Nombre del establecimiento", example = "Salón de eventos Primavera")
    private String name;

    @Schema(description = "Dirección del establecimiento", example = "Calle 123 #45-67")
    private String address;

    @Schema(description = "Teléfono de contacto del establecimiento", example = "3001234567")
    private String phone;

    @Schema(description = "Ciudad donde se encuentra", example = "Cali")
    private String city;

    @Schema(description = "Capacidad máxima de personas", example = "200")
    private Integer capacity;

    @Schema(description = "Tipo de establecimiento", example = "SMALL, MEDIUM, LARGE")
    private EstablishmentType type;

    @Schema(description = "Costo del alquiler", example = "1500.0")
    private Double cost;

    @Schema(description = "URL de imagen principal del establecimiento", example = "https://example.com/imagen.jpg")
    private String imageUrl;

    @Schema(description = "Estado del establecimiento (activo o no)", example = "true")
    private Boolean active;
}
