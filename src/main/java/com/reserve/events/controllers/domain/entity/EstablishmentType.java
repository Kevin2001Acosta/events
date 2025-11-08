package com.reserve.events.controllers.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo de establecimiento")
public enum EstablishmentType {
    @Schema(description = "Establecimiento pequeño")
    SMALL,

    @Schema(description = "Establecimiento mediano")
    MEDIUM,

    @Schema(description = "Establecimiento grande")
    LARGE
}
