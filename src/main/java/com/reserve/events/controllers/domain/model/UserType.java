package com.reserve.events.controllers.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipos de usuario")
public enum UserType {

    @Schema(description = "Usuario tipo Cliente")
    CLIENTE,

    @Schema(description = "Usuario tipo Administrador")
    ADMIN
}