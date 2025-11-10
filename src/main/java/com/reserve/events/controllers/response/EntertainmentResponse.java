package com.reserve.events.controllers.response;

import com.reserve.events.controllers.domain.model.TypeEntertainment;
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
@Schema(description = "Respuesta de la API para un servicio de entretenimiento")
public class EntertainmentResponse {
    @Id
    private String id;

    @Schema(description = "Nombre del entretenimiento")
    private String name;

    @Schema(description = "Tipo del entretenimiento")
    private TypeEntertainment type;

    @Schema(description = "Descripción del entretenimiento")
    private String description;

    @Schema(description = "Tarifa por hora del entretenimiento en pesos colombianos")
    private double hourlyRate;
}
