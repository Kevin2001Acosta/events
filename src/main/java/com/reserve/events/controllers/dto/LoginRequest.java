package com.reserve.events.controllers.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud de inicio de sesión de un usuario")
public class LoginRequest {

    @Email(message = "Debe ingresar un correo electrónico válido")
    @NotBlank(message = "El correo electrónico del usuario es obligatorio")
    @Schema(description = "Correo electrónico del usuario", example = "monica.cifuentes@correo.com")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "La contraseña del usuario para autenticación", example = "P@ssw0rd!")
    private String password;
}
