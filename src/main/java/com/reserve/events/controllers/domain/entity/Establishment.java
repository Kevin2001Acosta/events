package com.reserve.events.controllers.domain.entity;

import com.reserve.events.controllers.domain.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Establecimientos")
@Schema(description = "Entidad que representa un establecimiento")
public class Establishment {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotBlank(message = "El nombre del establecimiento es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Schema(description = "Nombre del establecimiento", example = "Salón de eventos Primavera")
    private String name;

    @NotBlank(message = "La dirección del establecimiento es obligatoria")
    @Size(max = 150, message = "La dirección no puede superar los 150 caracteres")
    @Schema(description = "Dirección del establecimiento", example = "Calle 123 #45-67")
    private String address;

    @NotBlank(message = "El teléfono del establecimiento es obligatorio")
    @Pattern(regexp = "\\d{7,10}", message = "El teléfono debe tener entre 7 y 10 dígitos")
    @Schema(description = "Teléfono de contacto del establecimiento", example = "3001234567")
    private String phone;

    @NotBlank(message = "La ciudad del establecimiento es obligatoria")
    @Size(max = 50, message = "La ciudad no puede superar los 50 caracteres")
    @Schema(description = "Ciudad donde se encuentra el establecimiento", example = "Bogotá")
    private String city;

    @NotNull(message = "La capacidad del establecimiento es obligatoria")
    @Min(value = 10, message = "La capacidad mínima es 10")
    @Max(value = 1000, message = "La capacidad máxima es 1000")
    @Schema(description = "Capacidad máxima del establecimiento", example = "200")
    private Integer capacity;

    @NotNull(message = "El tipo de establecimiento es obligatorio")
    @Schema(description = "Tipo de establecimiento", example = "SMALL", allowableValues = {"SMALL", "MEDIUM", "LARGE"})
    private EstablishmentType type;

    @NotNull(message = "El costo del establecimiento es obligatorio")
    @DecimalMin(value = "100.0", message = "El costo mínimo es 100")
    @DecimalMax(value = "20000.0", message = "El costo máximo es 10000")
    @Schema(description = "Costo de alquiler del establecimiento", example = "1500.0")
    private Double cost;

    @NotBlank(message = "La URL de la imagen del establecimiento es obligatoria")
    @Pattern(regexp = "^(http|https)://.*$", message = "La URL debe comenzar con http o https")
    @Schema(description = "URL de la imagen del establecimiento", example = "https://example.com/establishment-image.jpg")
    private String imageUrl;

    @NotNull
    @Schema(description = "Indica si el establecimiento está activo (borrado lógico)", example = "true")
    private Boolean active = true;

    @Builder.Default
    @Schema(description = "Lista de reservas asociadas al establecimiento")
    private List<ReserveSummary> bookings = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReserveSummary {
        @NotBlank(message = "El id de la reserva es obligatoria")
        @Schema(description = "Id de la reserva", example = "res_1")
        private String id;

        @NotNull(message = "El estado es obligatorio")
        @Schema(description = "Estado de la reserva", example = "PROGRAMADA",  allowableValues = {"PROGRAMADA", "COMPLETADA", "CANCELADA"})
        private StatusReserve status;

        @NotNull(message = "La información del cliente es obligatoria")
        @Schema(description = "Información mínima del cliente asociada a la reserva")
        private UserSummary user;

        @NotNull(message = "La información del evento es obligatoria")
        @Schema(description = "Información mínima del evento asociado a la reserva")
        private EventSummary event;

        @NotNull(message = "La(s) fecha(s) de reserva son obligatorias")
        @Size(min = 1, message = "Debe haber al menos una fecha")
        @Schema(description = "Fecha(s) de reserva", example = "[2025-03-03, 2025-03-04]")
        private List<LocalDate> dates;

        @Schema(description = "Información de los servicios cubiertos de la reserva")
        private CoveredServicesReserve services;
    }
}
