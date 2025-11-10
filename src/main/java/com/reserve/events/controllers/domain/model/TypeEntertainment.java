package com.reserve.events.controllers.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo de servicios de entretenimiento")
public enum TypeEntertainment {

    @Schema(description = "Servicio de Animadores")
    ANIMADORES,

    @Schema(description = "Servicio de Musicos")
    MUSICOS,

    @Schema(description = "Servicio de Bailarines")
    BAILARINES
}
