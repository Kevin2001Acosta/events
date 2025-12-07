package com.reserve.events.controllers.response;

import com.reserve.events.controllers.domain.entity.Decoration;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de la API para un servicio de decoración")
public class DecorationResponse {

    @Id
    private String id;

    @Schema(description = "Tema de la decoración")
    private String theme;

    @Schema(description = "Artículos de la decoración")
    private String articles;

    @Schema(description = "Costo de la decoración en pesos colombianos")
    private double cost;

    @Builder.Default
    @Schema(description = "Lista de reservas programadas que usan esta decoración")
    private List<Decoration.ReserveSummary> scheduledBookings = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Lista de reservas completadas que usaron esta decoración")
    private List<Decoration.ReserveSummary> completedBookings = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Lista de reservas canceladas que tenían esta decoración")
    private List<Decoration.ReserveSummary> cancelledBookings = new ArrayList<>();
}
