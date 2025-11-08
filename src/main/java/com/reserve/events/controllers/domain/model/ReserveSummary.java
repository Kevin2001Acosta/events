package com.reserve.events.controllers.domain.model;

import com.reserve.events.controllers.domain.entity.Reserve;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resumen de una reserva para ser incrustado en otras entidades")
public class ReserveSummary {

    @NotBlank(message = "El id de la reserva es obligatoria")
    @Schema(description = "Id de la reserva", example = "res_1")
    private String id;

    @NotNull(message = "El estado es obligatorio")
    @Schema(description = "Estado de la reserva", example = "PROGRAMADA",  allowableValues = {"PROGRAMADA", "COMPLETADA", "CANCELADA"})
    private StatusReserve status;

    @NotNull(message = "La información del evento es obligatoria")
    @Schema(description = "Información mínima del evento asociado a la reserva")
    private EventSummary event;

    @NotNull(message = "La información del establecimiento es obligatoria")
    @Schema(description = "Información mínima del establecimiento asociado a la reserva")
    private EstablishmentSummary establishment;

    @NotNull(message = "La(s) fecha(s) de reserva son obligatorias")
    @Schema(description = "Fecha(s) de reserva", example = "[2025-03-03, 2025-03-04]")
    private List<LocalDate> dates;

    @Schema(description = "Información de los servicios cubiertos de la reserva")
    private Reserve.CoveredServices services;
}
