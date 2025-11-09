package com.reserve.events.controllers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.reserve.events.controllers.domain.model.StatusReserve;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

/**
 * DTO para la creación y actualización de reservas
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Solicitud para crear o actualizar una reserva")
public class ReserveRequest {

    @NotNull(message = "El estado es obligatorio")
    @Schema(description = "Estado de la reserva", example = "PROGRAMADA",  allowableValues = {"PROGRAMADA", "COMPLETADA", "CANCELADA"})
    private StatusReserve status;

    @NotNull(message = "El número de invitados es obligatorio")
    @Schema(description = "Número de invitados", example = "80")
    private Integer guestNumber;



}
