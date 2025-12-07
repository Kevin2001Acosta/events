package com.reserve.events.controllers.domain.entity;

import com.reserve.events.controllers.domain.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Decoracion")
@Schema(description = "Entidad que representa un servicio de decoración que el cliente puede añadir en su reserva.")
public class Decoration {

    @Id
    private String id;

    @NotBlank(message = "El tema es obligatorio")
    @Schema(description = "Tema de la decoración", example = "Fiesta infantil")
    private String theme;

    @NotBlank(message = "Los artículos son obligatorios")
    @Schema(description = "Artículos de la decoración", example = "Globos, guirnaldas, mesa de dulces")
    private String articles;

    @NotNull(message = "El costo es obligatorio")
    @Schema(description = "Costo de la decoración en pesos colombianos", example = "100000")
    private double cost;

    @Builder.Default
    @Schema(description = "Lista de reservas programadas que usan esta decoración")
    private List<ReserveSummary> scheduledBookings = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Lista de reservas completadas que usaron esta decoración")
    private List<ReserveSummary> completedBookings = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Lista de reservas canceladas que tenían esta decoración")
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

        @NotNull(message = "La información del evento es obligatoria")
        @Schema(description = "Información mínima del evento asociado a la reserva")
        private EventSummary event;

        @NotNull(message = "La información del establecimiento es obligatoria")
        @Schema(description = "Información mínima del establecimiento asociado a la reserva")
        private EstablishmentSummary establishment;

        @NotNull(message = "Las fechas son obligatorias")
        @Schema(description = "Fecha(s) de reserva", example = "[2025-03-03, 2025-03-04]")
        private List<LocalDate> dates;
    }
}
