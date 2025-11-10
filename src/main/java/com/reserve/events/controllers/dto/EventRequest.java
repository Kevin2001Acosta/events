package com.reserve.events.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para crear o actualizar un evento")
public class EventRequest {

    @NotBlank(message = "El tipo de evento es obligatorio")
    @Schema(description = "Tipo de evento", example = "Cumpleaños")
    private String type;

}
