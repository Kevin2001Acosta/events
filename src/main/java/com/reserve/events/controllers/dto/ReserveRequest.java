package com.reserve.events.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.reserve.events.controllers.domain.entity.Reserve;
import com.reserve.events.controllers.domain.model.EstablishmentSummary;
import com.reserve.events.controllers.domain.model.EventSummary;
import com.reserve.events.controllers.domain.model.MenuCatering;
import com.reserve.events.controllers.domain.model.StatusReserve;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para la creación y actualización de reservas
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para crear o actualizar una reserva")
public class ReserveRequest {

    @Min(value = 1, message = "El número de invitados debe ser al menos 1")
    @NotNull(message = "El número de invitados es obligatorio")
    @Schema(
            description = "Número de invitados",
            example = "80",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer guestNumber;

    @NotNull(message = "La(s) fecha(s) de reserva son obligatorias")
    @Schema(
            description = "Fechas de reserva (una o más)",
            example = "[2025-03-03, 2025-03-04]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private List<@NotNull(message = "Las fecha de reserva no puede estar vacía") LocalDate> dates;

    @NotBlank
    @Schema(
            description = "Id del evento",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String eventId;

    @NotBlank
    @Schema(
            description = "Id del establecimiento",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String establishmentId;

    @Size(min = 2, max = 900, message = "El comentario debe tener entre 2 y 900 caracteres")
    @Schema(
            description = "Comentario sobre la reserva",
            example = "Comentario")
    private String comments;

    @Schema(
            description = "Servicios elegidos por el cliente para la reserva")
    private CoveredServicesRequest services;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Servicios seleccionados por el cliente")
    public static class CoveredServicesRequest {

        @Builder.Default
        @Schema(description = "Lista de servicios de entretenimiento seleccionados")
        private List<EntertainmentRequest> entertainment = new ArrayList<>();

        @Schema(description = "Decoración seleccionada")
        private DecorationRequest decoration;

        @Builder.Default
        @Schema(description = "Lista de servicios de catering seleccionados")
        private List<CateringRequest> catering = new ArrayList<>();

        @Builder.Default
        @Schema(description = "Lista de servicios adicionales seleccionados")
        private List<AdditionalServiceRequest> additionalServices = new ArrayList<>();
    }

    public static class EntertainmentRequest {

        @Schema(description = "ID del servicio")
        @NotBlank(message = "El id del servicio de entretenimiento es obligatorio")
        private String id;

        @NotNull(message = "Las horas contratadas son obligatorias")
        @Min(value = 1, message = "Las horas deben ser mayores a 0")
        @Schema(
                description = "Horas contratadas del servicio",
                example = "3")
        private int hours;
    }

    public static class DecorationRequest {

        @Schema(description = "ID de la decoración")
        @NotBlank(message = "El id de la decoración es obligatorio")
        private String id;
    }

    public static class CateringRequest {

        @Schema(description = "ID del Caterín")
        @NotBlank(message = "El id del servicio de catering es obligatorio")
        private String id;

        @Schema(
                description = "Tipo de menú elegido",
                example = "VEGETARIANO",
                allowableValues = {"BUFFET", "VEGETARIANO", "INFANTIL", "GOURMET"})
        @NotNull(message = "El tipo de menú es obligatorio")
        private MenuCatering menuType;

        @Schema(
                description = "Número de platos requerido",
                example = "10")
        @NotNull(message = "El número de platos es obligatorio")
        @Min(value = 1, message = "El número de platos debe ser mayor a 0")
        private int numberDish;
    }

    public static class AdditionalServiceRequest {

        @Schema(description = "Horas contratadas del servicio", example = "3")
        @NotBlank(message = "El id del servicio adicional es obligatorio")
        private String id;
    }
}
