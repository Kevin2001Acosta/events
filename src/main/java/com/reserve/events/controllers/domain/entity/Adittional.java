package com.reserve.events.controllers.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Adittional")
@Schema(description = "Entidad que representa un servicio adicional de los ofrecidos (entretenimiento, decoración y caterin) que el cliente puede añadir en su reserva.")
public class Adittional {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del servicio adicional", example = "Fotógrafo profesional")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    @Schema(description = "Descripción del servicio adicional", example = "Servicio de fotografía profesional para eventos")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String description;

    @NotNull(message = "El costo es obligatorio")
    @Schema(description = "Costo del servicio adicional", example = "300000")
    private double cost;
}
