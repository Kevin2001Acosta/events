package com.reserve.events.controllers.domain.entity;

import com.reserve.events.controllers.domain.model.MenuCatering;
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
@Document(collection = "Caterin")
@Schema(description = "Entidad que representa un servicio de comidas y bebidas que el cliente puede añadir en su reserva.")
public class Catering {

    @Id
    private String id;

    @NotNull(message = "El tipo de menu es obligatorio")
    @Schema(description = "Tipo de menu del servicio de comida", example = "INFANTIL", allowableValues = {"BUFFET", "VEGETARIANO", "INFANTIL", "GOURMET"})
    private MenuCatering menuType;

    @NotBlank(message = "La descripción es obligatoria")
    @Schema(description = "Descripción del servicio de comida", example = "Catering infantil premium con menú saludable: mini wraps de pollo, ensalada de frutas y limonada. Incluye estación de postres.")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String description;

    @NotNull(message = "El costo del plato es obligatorio")
    @Schema(description = "El costo por plato requerido por el cliente en el servicio de comida", example = "80")
    private double costDish;
}
