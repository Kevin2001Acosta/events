package com.reserve.events.controllers.response;

import com.reserve.events.controllers.domain.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de la API para una reserva")
public class ReserveResponse {

    @Id
    private String id;

    @Schema(description = "Estado de la reserva")
    private StatusReserve status;

    @Schema(description = "Fecha(s) de reserva")
    private List<LocalDate> dates;

    @Schema(description = "Número de invitados")
    private Integer guestNumber;

    @Schema(description = "Información del cliente")
    private UserSummary client;

    @Schema(description = "Información del evento")
    private EventSummary event;

    @Schema(description = "Información del establecimiento")
    private EstablishmentSummary establishment;

    @Schema(description = "Información de los servicios de la reserva")
    private CoveredServicesReserve services;

    @Schema(description = "Costo total de la reserva")
    private double totalCost;

    @Schema(description = "Comentarios de la reserva")
    private String comments;
}
