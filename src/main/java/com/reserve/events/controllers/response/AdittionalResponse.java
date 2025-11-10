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
@Schema(description = "Respuesta de la API para un servicio adicional")
public class AdittionalResponse {

    @Id
    private String id;

    @Schema(description = "Nombre del servicio adicional")
    private String name;

    @Schema(description = "Descripción del servicio adicional")
    private String description;

    @Schema(description = "Costo del servicio adicional")
    private double cost;
}
