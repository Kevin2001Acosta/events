package com.reserve.events.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para crear o solicitar un reporte")
public class ReportRequest {

    @NotBlank(message = "El tipo de reporte es obligatorio")
    @Schema(description = "Tipo de reporte", example = "reservationReport", allowableValues = {"reservationReport", "incomeReport", "serviceReport", "establishmentUsageReport"})
    private String type;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Schema(description = "Fecha de inicio del período", example = "2025-11-01")
    private LocalDate from;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Schema(description = "Fecha de fin del período", example = "2025-11-30")
    private LocalDate to;

    @Schema(description = "Filtros adicionales para el reporte (dependen del tipo)")
    private Map<String, Object> filters;

    @Schema(description = "Nombre/descripción del reporte", example = "Reporte de reservas noviembre")
    private String name;
}
