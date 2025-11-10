package com.reserve.events.controllers.response;

import com.reserve.events.controllers.domain.model.MenuCatering;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de la API para un servicio de Caterin")
public class CateringResponse {

    @Id
    private String id;

    @Schema(description = "Tipo de menu del servicio de comida")
    private MenuCatering menuType;

    @Schema(description = "Descripción del servicio de comida")
    private String description;

    @Schema(description = "El costo por plato requerido por el cliente en el servicio de comida")
    private double costDish;
}
