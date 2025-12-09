package com.reserve.events.controllers.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO de respuesta para un reporte")
public class ReportResponse {

    @Schema(description = "ID único del reporte", example = "650f3a...")
    private String id;

    @Schema(description = "Tipo de reporte", example = "reservationReport")
    private String type;

    @Schema(description = "Fecha de inicio del período", example = "2025-11-01")
    private LocalDate from;

    @Schema(description = "Fecha de fin del período", example = "2025-11-30")
    private LocalDate to;

    @Schema(description = "Fecha y hora de creación", example = "2025-12-08T10:00:00Z")
    private LocalDateTime createdAt;

    @Schema(description = "Nombre/descripción del reporte")
    private String name;

    @Schema(description = "Datos del reporte según su tipo")
    private Map<String, Object> data;

    @Schema(description = "Metadatos del reporte")
    private ReportMetadataResponse metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReportMetadataResponse {

        @Schema(description = "Duración de la generación en ms", example = "1234")
        private Long durationMs;

        @Schema(description = "Cantidad de registros procesados", example = "452")
        private Integer rowCount;

        @Schema(description = "Estado del reporte", example = "completed")
        private String status;

        @Schema(description = "Mensaje de error si aplica")
        private String errorMessage;
    }
}
