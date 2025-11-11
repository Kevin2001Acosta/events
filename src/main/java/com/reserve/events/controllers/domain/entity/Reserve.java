package com.reserve.events.controllers.domain.entity;

import com.reserve.events.controllers.domain.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Reserva")
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

    @NotNull(message = "La(s) fecha(s) de reserva son obligatorias")
    @Schema(description = "Fecha(s) de reserva", example = "[2025-03-03, 2025-03-04]")
    private List<LocalDate> dates;

    @NotNull(message = "El costo total es obligatorio")
    @Schema(description = "Costo total de la reserva", example = "900000")
    private double totalCost;

    @Schema(description = "Comentarios sobre la reserva", example = "Comentario")
    private String comments;

    @NotNull(message = "El cliente es obligatorio")
    @Schema(description = "Información del cliente que hace la reserva")
    private UserSummary client;

    @NotNull(message = "El evento es obligatorio")
    @Schema(description = "Información del evento relacionado a la reserva")
    private EventSummary event;

    @NotNull(message = "El establecimiento es obligatorio")
    @Schema(description = "Información del establecimiento donde se va a realizar la reserva")
    private EstablishmentSummary establishment;

    @Schema(description = "Información de los servicios cubiertos de la reserva")
    private CoveredServicesReserve services;
}
