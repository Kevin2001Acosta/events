package com.reserve.events.controllers.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Decoracion")
@Schema(description = "Entidad que representa un servicio de decoración que el cliente puede añadir en su reserva.")
public class Decoration {

    @Id
    private String id;

    @NotBlank(message = "El tema es obligatorio")
    @Schema(description = "Tema de la decoración", example = "Fiesta infantil")
    private String theme;

    @NotBlank(message = "Los artículos son obligatorios")
    @Schema(description = "Artículos de la decoración", example = "Globos, guirnaldas, mesa de dulces")
    private String articles;

    @NotNull(message = "El costo es obligatorio")
    @Schema(description = "Costo de la decoración en pesos colombianos", example = "100000")
    private double cost;
}
