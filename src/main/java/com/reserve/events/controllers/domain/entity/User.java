package com.reserve.events.controllers.domain.entity;

import com.reserve.events.controllers.domain.model.PaymentStatus;
import com.reserve.events.controllers.domain.model.ReserveSummary;
import com.reserve.events.controllers.domain.model.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="Usuarios")
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
    @Schema(description = "Tipo de usuario", example = "CLIENTE", allowableValues = {"CLIENTE", "ADMIN"})
    private UserType type;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "La contraseña del usuario para autenticación", example = "P@ssw0rd!")
    private String Password;

    @Schema(description = "Lista de las reservas hechas por el usuario")
    private List<ReserveSummary> eventBookings;

    @Schema(description = "Lista de los pagos realizados por el usuario")
    private List<PaymentInfo> payments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información de un pago realizado por el usuario")
    public static class PaymentInfo {

        @NotBlank(message = "El id del pago es obligatorio")
        @Schema(description = "Id del pago", example = "pay_123")
        private String id;

        @Schema(description = "Estado del pago", example = "PAID", allowableValues = {"PENDIENTE", "COMPLETADO", "CANCELADO"})
        private PaymentStatus status;

        @Schema(description = "Descripción del pago", example = "Pago inicial por reserva de salón")
        private String description;

        @Schema(description = "Costo total del pago", example = "1500.0")
        private Double totalCost;
    }
}
