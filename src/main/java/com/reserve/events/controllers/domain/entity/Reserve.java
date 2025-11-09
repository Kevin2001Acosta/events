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
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Reserve")
@Schema(description = "Entidad que representa una reserva de un cliente.")
public class Reserve {

    @Id
    private String id;

    @NotNull(message = "El estado es obligatorio")
    @Schema(description = "Estado de la reserva", example = "PROGRAMADA",  allowableValues = {"PROGRAMADA", "COMPLETADA", "CANCELADA"})
    private StatusReserve status;

    @NotNull(message = "El número de invitados es obligatorio")
    @Schema(description = "Número de invitados", example = "80")
    private Integer guestNumber;

    @Builder.Default
    @NotNull(message = "La(s) fecha(s) de reserva son obligatorias")
    @Schema(description = "Fecha(s) de reserva", example = "[2025-03-03, 2025-03-04]")
    private List<LocalDate> dates;

    @NotNull(message = "El costo total es obligatorio")
    @Schema(description = "Costo total de la reserva", example = "900000")
    private double totalCost;

    @Schema(description = "Comentarios sobre la reserva", example = "Comentario")
    private String comments;

    @Schema(description = "Información del cliente que hace la reserva")
    private UserSummary client;

    @Schema(description = "Información del evento relacionado a la reserva")
    private EventSummary event;

    @Schema(description = "Información del establecimiento donde se va a realizar la reserva")
    private EstablishmentSummary establishment;

    @Schema(description = "Información de los servicios cubiertos de la reserva")
    private CoveredServices services;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoveredServices {

        @Builder.Default
        @Schema(description = "Servicios de entretenimiento incluidos en este bloque")
        private List<EntertainmentSummary> entertainment = new ArrayList<>();

        @Builder.Default
        @Schema(description = "Decoración incluida en este bloque")
        private DecorationSummary decoration;

        @Builder.Default
        @Schema(description = "Catering incluido en este bloque")
        private List<CateringSummary> catering = new ArrayList<>();

        @Builder.Default
        @Schema(description = "Servicios adicionales incluidos en este bloque")
        private List<AdittionalSummary> additionalServices = new ArrayList<>();
    }
}
