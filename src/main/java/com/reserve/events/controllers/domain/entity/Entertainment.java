package com.reserve.events.controllers.domain.entity;

import com.reserve.events.controllers.domain.model.TypeEntertainment;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Document(collection = "Entretenimiento")
@Schema(description = "Entidad que representa un servicio de entretenimiento que el cliente puede añadir en su reserva.")
public class Entertainment {

    @Id
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    @Schema(description = "Nombre del entretenimiento", example = "Payasos")
    private String name;

    @NotBlank(message = "El tipo es obligatorio")
    @Schema(description = "Tipo del entretenimiento", example = "ANIMADORES",  allowableValues = {"ANIMADORES", "MUSICOS", "BAILARINES"})
    private TypeEntertainment type;

    @NotBlank(message = "La descripción es obligatoria")
    @Schema(description = "Descripción del entretenimiento", example = "Animación infantil con payasos que realizan juegos interactivos, concursos y dinámicas para niños de todas las edades. Ideal para cumpleaños y eventos familiares.")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String description;

    @NotNull(message = "La tarifa por hora es obligatoria")
    @Schema(description = "Tarifa por hora del entretenimiento en pesos colombianos", example = "10000")
    private double hourlyRate;
}
