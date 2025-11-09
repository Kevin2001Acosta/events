package com.reserve.events.controllers.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estado de la reserva")
public enum StatusReserve {

    @Schema(description = "Reserva Programada")
    PROGRAMADA,

    @Schema(description = "Reserva Completada")
    COMPLETADA,

    @Schema(description = "Reserva Cancelada")
    CANCELADA
}
