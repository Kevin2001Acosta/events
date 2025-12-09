package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Report;
import com.reserve.events.controllers.domain.repository.ReportRepository;
import com.reserve.events.controllers.dto.ReportRequest;
import com.reserve.events.controllers.dto.ReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final MongoTemplate mongoTemplate;
    private final ReserveService reserveService;
    private final PaymentService paymentService;

    // Genera un reporte basado en el tipo solicitado
    public ReportResponse generateReport(ReportRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            Map<String, Object> data = switch (request.getType()) {
                case "reservationReport" -> generateReservationReport(request);
                case "incomeReport" -> generateIncomeReport(request);
                case "serviceReport" -> generateServiceReport(request);
                case "establishmentUsageReport" -> generateEstablishmentUsageReport(request);
                default -> throw new IllegalArgumentException("Tipo de reporte no válido: " + request.getType());
            };

            long endTime = System.currentTimeMillis();

            Report report = Report.builder()
                    .type(request.getType())
                    .period(Report.Period.builder()
                            .from(request.getFrom())
                            .to(request.getTo())
                            .build())
                    .createdAt(LocalDateTime.now(ZoneId.of("UTC")))
                    .data(data)
                    .metadata(Report.ReportMetadata.builder()
                            .durationMs(endTime - startTime)
                            .rowCount(calculateRowCount(data))
                            .status("completed")
                            .build())
                    .build();

            Report savedReport = reportRepository.save(report);
            return mapToResponse(savedReport);

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            Report failedReport = Report.builder()
                    .type(request.getType())
                    .period(Report.Period.builder()
                            .from(request.getFrom())
                            .to(request.getTo())
                            .build())
                    .createdAt(LocalDateTime.now(ZoneId.of("UTC")))
                    .data(new HashMap<>())
                    .metadata(Report.ReportMetadata.builder()
                            .durationMs(endTime - startTime)
                            .status("failed")
                            .errorMessage(e.getMessage())
                            .build())
                    .build();

            Report savedFailedReport = reportRepository.save(failedReport);
            return mapToResponse(savedFailedReport);
        }
    }

    // Genera reporte de reservas
    private Map<String, Object> generateReservationReport(ReportRequest request) {
        Map<String, Object> reportData = new HashMap<>();

        // TODO: Implementar agregaciones reales contra MongoDB con request.getFrom() y request.getTo()

        // Ejemplo: estructura de datos que deberías poblar desde MongoDB
        // Reemplaza esto con tus agregaciones reales contra "Reserva" collection
        reportData.put("totalReservations", 0);
        reportData.put("byStatus", new HashMap<String, Integer>() {{
            put("PROGRAMADA", 0);
            put("COMPLETADA", 0);
            put("CANCELADA", 0);
        }});
        reportData.put("topEventTypes", new ArrayList<>());

        return reportData;
    }

    // Genera reporte de ingresos
    private Map<String, Object> generateIncomeReport(ReportRequest request) {
        Map<String, Object> reportData = new HashMap<>();

        // Agrupa pagos por establecimiento en el período
        // Reemplaza esto con tus agregaciones reales contra "Pagos" collection
        reportData.put("totalIncome", 0.0);
        reportData.put("currency", "COP");
        reportData.put("incomeByEstablishment", new ArrayList<>());
        reportData.put("paymentsCount", 0);

        return reportData;
    }

    // Genera reporte de servicios
    private Map<String, Object> generateServiceReport(ReportRequest request) {
        Map<String, Object> reportData = new HashMap<>();

        // Analiza uso y ingresos por tipo de servicio (catering, decoración, etc.)
        // Reemplaza esto con tus agregaciones reales
        reportData.put("mostUsedServices", new ArrayList<>());
        reportData.put("serviceIncome", new ArrayList<>());

        return reportData;
    }

    // Genera reporte de uso de establecimientos
    private Map<String, Object> generateEstablishmentUsageReport(ReportRequest request) {
        Map<String, Object> reportData = new HashMap<>();

        // Agrupa reservas por establecimiento
        // Reemplaza esto con tus agregaciones reales
        reportData.put("usageList", new ArrayList<>());
        reportData.put("mostUsedEstablishment", null);

        return reportData;
    }

    // Obtiene todos los reportes del usuario
    public List<ReportResponse> getAllReports() {
        return reportRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Obtiene un reporte específíco por ID
    public ReportResponse getReportById(String reportId) {
        return reportRepository.findById(reportId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado con ID: " + reportId));
    }

    // Obtiene reportes por tipo
    public List<ReportResponse> getReportsByType(String type) {
        return reportRepository.findByType(type)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Elimina un reporte
    public void deleteReport(String reportId) {
        reportRepository.deleteById(reportId);
    }

    // Calcula la cantidad de registros en los datos del reporte
    private Integer calculateRowCount(Map<String, Object> data) {
        int count = 0;
        for (Object value : data.values()) {
            if (value instanceof List<?> list) {
                count += list.size();
            } else if (value instanceof Map) {
                count += ((Map<?, ?>) value).size();
            }
        }
        return Math.max(count, 1);
    }

    // Mapea entidad Report a DTO ReportResponse
    private ReportResponse mapToResponse(Report report) {
        ReportResponse.ReportMetadataResponse metadata = null;
        if (report.getMetadata() != null) {
            metadata = ReportResponse.ReportMetadataResponse.builder()
                    .durationMs(report.getMetadata().getDurationMs())
                    .rowCount(report.getMetadata().getRowCount())
                    .status(report.getMetadata().getStatus())
                    .errorMessage(report.getMetadata().getErrorMessage())
                    .build();
        }

        return ReportResponse.builder()
                .id(report.getId())
                .type(report.getType())
                .from(report.getPeriod().getFrom())
                .to(report.getPeriod().getTo())
                .createdAt(report.getCreatedAt())
                .data(report.getData())
                .metadata(metadata)
                .build();
    }
}
