package com.reserve.events.controllers.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Eventos")
@Schema(description = "Entidad que representa un evento")
public class Event {

    @Id
    private String id;

    @NotBlank(message = "El tipo de evento es obligatorio")
    @Schema(description = "Tipo de evento", example = "Cumpleaños")
    private String type;

    @NotBlank(message = "La URL de la imagen del evento es obligatoria")
    @Schema(description = "URL de la imagen del evento", example = "https://example.com/event-image.jpg")
    private String imageUrl;
}