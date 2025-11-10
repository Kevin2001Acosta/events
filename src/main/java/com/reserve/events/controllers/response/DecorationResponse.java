package com.reserve.events.controllers.response;

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
@Schema(description = "Respuesta de la API para un servicio de decoración")
public class DecorationResponse {

    @Id
    private String id;

    @Schema(description = "Tema de la decoración")
    private String theme;

    @Schema(description = "Artículos de la decoración")
    private String articles;

    @Schema(description = "Costo de la decoración en pesos colombianos")
    private double cost;
}
