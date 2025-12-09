package com.reserve.events.controllers;

import com.reserve.events.application.ReportService;
import com.reserve.events.controllers.dto.ReportRequest;
import com.reserve.events.controllers.dto.ReportResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Endpoints para gestionar reportes analíticos")
public class ReportController {

    private final ReportService reportService;

    // Genera un nuevo reporte basado en los parámetros proporcionados
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Generar un nuevo reporte", description = "Genera un reporte analítico del tipo especificado para el período indicado")
    public ResponseEntity<ReportResponse> generateReport(
            @Valid @RequestBody ReportRequest request) {
        ReportResponse response = reportService.generateReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Obtiene todos los reportes del usuario autenticado
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar todos los reportes", description = "Obtiene todos los reportes generados")
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        List<ReportResponse> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    // Obtiene reportes filtrados por tipo
    @GetMapping("/by-type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar reportes por tipo", description = "Obtiene todos los reportes de un tipo específico")
    public ResponseEntity<List<ReportResponse>> getReportsByType(
            @Parameter(description = "Tipo de reporte", example = "reservationReport")
            @PathVariable String type) {
        List<ReportResponse> reports = reportService.getReportsByType(type);
        return ResponseEntity.ok(reports);
    }

    // Obtiene un reporte específíco por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Obtener reporte por ID", description = "Obtiene los detalles completos de un reporte específico")
    public ResponseEntity<ReportResponse> getReportById(
            @Parameter(description = "ID del reporte")
            @PathVariable String id) {
        ReportResponse report = reportService.getReportById(id);
        return ResponseEntity.ok(report);
    }

    // Elimina un reporte específíco
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar reporte", description = "Elimina un reporte específico (solo administradores)")
    public ResponseEntity<Void> deleteReport(
            @Parameter(description = "ID del reporte")
            @PathVariable String id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
