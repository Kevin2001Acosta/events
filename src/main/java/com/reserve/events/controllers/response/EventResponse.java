package com.reserve.events.controllers.response;

import com.reserve.events.controllers.domain.entity.Event;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @Builder.Default
    @Schema(description = "Lista de reservas programadas del evento")
    private List<Event.ReserveSummary> scheduledBookings = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Lista de reservas completadas del evento")
    private List<Event.ReserveSummary> completedBookings = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Lista de reservas canceladas del evento")
    private List<Event.ReserveSummary> cancelledBookings = new ArrayList<>();
}