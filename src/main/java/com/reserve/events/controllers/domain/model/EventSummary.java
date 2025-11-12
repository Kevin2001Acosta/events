package com.reserve.events.controllers.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resumen de un evento para ser incrustado en otras entidades")
public class EventSummary {

    @NotBlank(message = "El id del evento es obligatorio")
    @Schema(description = "Id del evento", example = "evt_1")
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "El tipo de evento es obligatorio")
    @Schema(description = "Tipo de evento", example = "Cumpleaños")
    private String type;
}
