package com.reserve.events.controllers.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="usuarios")
@Schema(description = "Entidad que representa a un usuario, sus pagos y reservas hechas")
public class User {

    @Id
    private String id;

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

    @NotBlank(message = "El tipo de usuario es obligatorio")
    @Schema(description = "Tipo de usuario", example = "regular", allowableValues = {"client", "admin"})
    private String type;


    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "La contraseña del usuario para autenticación", example = "P@ssw0rd!")
    private String Password;

    // posibles nombres de las clases de los objetos de reservas y pagos, ambos tendrán servicios cubiertos
    // TODO: Sería mejor poner solo el nombre del servicio en pagos y poner los datos de los servicios.
    // private List<eventBookingSummary>  eventBookings;
    // private List<paymentSummary> payments;


}
