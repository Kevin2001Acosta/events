package com.reserve.events.controllers.domain.entity;

import com.reserve.events.controllers.domain.model.StatusReserve;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Reserve")
@Schema(description = "Entidad que representa una reserva de un cliente.")
public class Reserve {

    @Id
    private String id;

    @NotBlank(message = "El estado es obligatorio")
    @Schema(description = "Estado de la reserva", example = "PROGRAMADA",  allowableValues = {"PROGRAMADA", "COMPLETADA", "CANCELADA"})
    private StatusReserve status;

    @NotNull(message = "El número de invitados es obligatorio")
    @Schema(description = "Número de invitados", example = "80")
    private int guestNumber;

    private double totalCost;

    private String comments;




    // private EventSummary event;
}
