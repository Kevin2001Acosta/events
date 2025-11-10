package com.reserve.events.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    @Schema(description = "ID del evento", example = "507f1f77bcf86cd799439011")
    private String id;

    @Schema(description = "Tipo de evento", example = "Cumpleaños")
    private String type;

    @Schema(description = "URL de la imagen del evento", example = "https://example.com/event-image.jpg")
    private String imageUrl;
}