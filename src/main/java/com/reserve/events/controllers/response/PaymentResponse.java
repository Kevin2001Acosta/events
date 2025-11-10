package com.reserve.events.controllers.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.reserve.events.controllers.domain.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Respuesta de la API para un pago")
public class PaymentResponse {

    @Id
    @Schema(description = "Identificador único del pago")
    private String id;

    @Schema(description = "Descripción del evento asociado al pago")
    private String description;

    @Schema(description = "Estado actual del pago (Pendiente, Completado, Cancelado)")
    private PaymentStatus status;

    @Schema(description = "Costo total del servicio de reserva")
    private double totalCost;

    @Schema(description = "Información del cliente asociado al pago")
    private UserSummary client;

    @Schema(description = "Información de la reserva relacionada con el pago")
    private ReserveSummary reserve;

    @Schema(description = "Servicios cubiertos en el evento pagado")
    private CoveredServicesReserve coveredServices;
}
