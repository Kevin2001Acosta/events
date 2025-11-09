package com.reserve.events.controllers.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoveredServicesReserve {

    @Builder.Default
    @Schema(description = "Servicios de entretenimiento incluidos en este bloque")
    private List<EntertainmentSummary> entertainment = new ArrayList<>();

    @Schema(description = "Decoración incluida en este bloque")
    private DecorationSummary decoration;

    @Builder.Default
    @Schema(description = "Catering incluido en este bloque")
    private List<CateringSummary> catering = new ArrayList<>();

    @Builder.Default
    @Schema(description = "Servicios adicionales incluidos en este bloque")
    private List<AdittionalSummary> additionalServices = new ArrayList<>();
}
