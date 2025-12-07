package com.reserve.events.controllers.domain.entity;

import com.reserve.events.controllers.domain.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Eventos")
@Schema(description = "Entidad que representa un evento")
public class Event {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "El tipo de evento es obligatorio")
    @Schema(description = "Tipo de evento", example = "Cumpleaños")
    private String type;

    @NotBlank(message = "La URL de la imagen del evento es obligatoria")
    @Schema(description = "URL de la imagen del evento", example = "https://example.com/event-image.jpg")
    private String imageUrl;

    @Builder.Default
    @Schema(description = "Lista de reservas programadas del evento")
    private List<ReserveSummary> scheduledBookings = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Lista de reservas completadas del evento")
    private List<ReserveSummary> completedBookings = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Lista de reservas canceladas del evento")
    private List<ReserveSummary> cancelledBookings = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReserveSummary {
        @NotBlank(message = "El id de la reserva es obligatoria")
        @Schema(description = "Id de la reserva", example = "res_1")
        private String id;

        @NotNull(message = "El estado es obligatorio")
        @Schema(description = "Estado de la reserva", example = "PROGRAMADA", allowableValues = {"PROGRAMADA", "COMPLETADA", "CANCELADA"})
        private StatusReserve status;

        @NotNull(message = "La información del cliente es obligatoria")
        @Schema(description = "Información mínima del cliente asociada a la reserva")
        private UserSummary user;

        @NotNull(message = "La información del establecimiento es obligatoria")
        @Schema(description = "Información mínima del establecimiento asociado a la reserva")
        private EstablishmentSummary establishment;

        @NotNull(message = "Las fechas son obligatorias")
        @Schema(description = "Fecha(s) de reserva", example = "[2025-03-03, 2025-03-04]")
        private List<LocalDate> dates;

        @Schema(description = "Servicios de la reserva")
        private CoveredServicesReserve services;
    }
}