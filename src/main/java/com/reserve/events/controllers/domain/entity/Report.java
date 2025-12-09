package com.reserve.events.controllers.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reports")
@Schema(description = "Entidad que representa un reporte analítico generado del sistema.")
public class Report {

    @Id
    private String id;

    @NotBlank(message = "El tipo de reporte es obligatorio")
    @Schema(description = "Tipo de reporte", example = "reservationReport", allowableValues = {"reservationReport", "incomeReport", "serviceReport", "establishmentUsageReport"})
    private String type;

    @NotNull(message = "El período del reporte es obligatorio")
    @Schema(description = "Período del reporte (rango de fechas)")
    private Period period;

    @NotNull(message = "La fecha de creación es obligatoria")
    @Schema(description = "Fecha y hora en que se generó el reporte", example = "2025-12-08T10:00:00Z")
    private LocalDateTime createdAt;

    @Schema(description = "Nombre o descripción del reporte", example = "Reporte de reservas noviembre")
    private String name;

    @NotNull(message = "Los datos del reporte son obligatorios")
    @Schema(description = "Datos específicos del reporte según su tipo")
    private Map<String, Object> data;

    @Schema(description = "Metadatos del reporte (duración, cantidad de registros procesados)")
    private ReportMetadata metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Period {

        @NotNull(message = "La fecha de inicio es obligatoria")
        @Schema(description = "Fecha de inicio del período", example = "2025-11-01")
        private LocalDate from;

        @NotNull(message = "La fecha de fin es obligatoria")
        @Schema(description = "Fecha de fin del período", example = "2025-11-30")
        private LocalDate to;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReportMetadata {

        @Schema(description = "Duración de la generación del reporte en milisegundos", example = "1234")
        private Long durationMs;

        @Schema(description = "Cantidad de registros procesados", example = "452")
        private Integer rowCount;

        @Schema(description = "Estado del reporte", example = "completed", allowableValues = {"pending", "running", "completed", "failed"})
        private String status;

        @Schema(description = "Mensaje de error si el reporte falló", example = "")
        private String errorMessage;
    }
}
