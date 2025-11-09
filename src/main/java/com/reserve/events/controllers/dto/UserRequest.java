package com.reserve.events.controllers.dto;

import com.reserve.events.controllers.domain.model.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Clase que representa un usuario en el sistema.
 *
 * Atributos:
 * - fullName: Nombre completo del usuario.
 * - email: Correo electrónico del usuario.
 * - phone: Teléfono del usuario.
 * - city: Ciudad de residencia.
 * - type: Tipo de usuario (ADMIN, CLIENT, etc.).
 * - Password: Contraseña del usuario.
 *
 * Funcionalidad:
 * Esta clase se utiliza para almacenar y gestionar la información de los usuarios.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para crear o actualizar los datos de un usuario")
public class UserRequest {

    @NotBlank(message = "El nombre del usuario es obligatorio")
    @Schema(description = "Nombre completo del usuario", example = "Mónica Andrea Cifuentes Salcedo")
    private String fullName;

    @Email(message = "Debe ingresar un correo electrónico válido")
    @NotBlank(message = "El correo electrónico del usuario es obligatorio")
    @Schema(description = "Correo electrónico del usuario", example = "monica.cifuentes@correo.com")
    private String email;

    @NotBlank(message = "El número de teléfono es obligatorio")
    @Schema(description = "Número de teléfono del usuario", example = "3243685898")
    private String phone;

    @Schema(description = "Ciudad donde vive", example = "Medellín")
    private String city;

    @NotNull(message = "El tipo de usuario es obligatorio")
    @Schema(description = "Tipo de usuario", example = "CLIENTE", allowableValues = {"CLIENTE", "ADMIN"})
    private UserType type;

    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "La contraseña debe tener al menos 8 caracteres, una letra mayúscula, una letra minúscula, un número y un carácter especial"
    )
    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "La contraseña del usuario para autenticación", example = "P@ssw0rd!")
    private String password;


}
