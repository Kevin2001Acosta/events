package com.reserve.events.controllers.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estado del pago", allowableValues = {"Pendiente", "Completado", "Cancelado"})
public enum PaymentStatus {

    @Schema(description = "Pago Pendiente")
    PENDIENTE,

    @Schema(description = "Pago Completado")
    COMPLETADO,

    @Schema(description = "Pago Cancelado")
    CANCELADO
}
