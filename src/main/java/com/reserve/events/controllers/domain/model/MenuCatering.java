package com.reserve.events.controllers.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Menús del caterín")
public enum MenuCatering {

    @Schema(description = "Buffet")
    BUFFET,

    @Schema(description = "Menú Vegetariano")
    VEGETARIANO,

    @Schema(description = "Menú Infantil")
    INFANTIL,

    @Schema(description = "Menú Gourmet")
    GOURMET
}
