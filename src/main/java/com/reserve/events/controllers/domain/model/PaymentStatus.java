package com.reserve.events.controllers.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estado del pago", allowableValues = {"Pendiente", "Completado", "Cancelado"})
public enum PaymentStatus {
    PENDIENTE,
    COMPLETADO,
    CANCELADO
}
