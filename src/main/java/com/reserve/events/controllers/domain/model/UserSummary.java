package com.reserve.events.controllers.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummary implements Serializable{
    @Schema(description = "Id del cliente", example = "client_123")
    private String id;

    @Schema(description = "Nombre del cliente", example = "Juan Pérez")
    private String name;

    @Schema(description = "Correo del cliente", example = "juan@example.com")
    private String email;

    @Schema(description = "Teléfono del cliente", example = "3001234567")
    private String phone;
}
