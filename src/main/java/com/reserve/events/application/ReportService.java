package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Report;
import com.reserve.events.controllers.domain.repository.ReportRepository;
import com.reserve.events.controllers.dto.ReportRequest;
import com.reserve.events.controllers.dto.ReportResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.FacetOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.domain.Sort;
import org.bson.Document;
import com.reserve.events.controllers.dto.ReservationReportFacet;
import com.reserve.events.controllers.dto.TotalReservations;
import com.reserve.events.controllers.dto.StatusCount;
import com.reserve.events.controllers.dto.EventTypeCount;
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
                    .name(request.getName())
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
                    .name(request.getName())
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

        // Construir match por rango de fechas (elemento del array 'dates' dentro del documento Reserva)
        MatchOperation match = Aggregation.match(
                org.springframework.data.mongodb.core.query.Criteria.where("dates").gte(request.getFrom()).lte(request.getTo())
        );

        // Facet para obtener total, conteo por estado y top tipos de evento en una sola consulta
        FacetOperation facet = Aggregation.facet(
                        Aggregation.count().as("total")
                ).as("totalReservations")
                .and(
                        Aggregation.group("status").count().as("count")
                ).as("byStatus")
                .and(
                        Aggregation.group("event.type").count().as("count"),
                        Aggregation.sort(Sort.by(Sort.Direction.DESC, "count")),
                        Aggregation.limit(5)
                ).as("topEventTypes");

        Aggregation agg = Aggregation.newAggregation(match, facet);

        AggregationResults<ReservationReportFacet> results = mongoTemplate.aggregate(agg, "Reserva", ReservationReportFacet.class);
        ReservationReportFacet mapped = results.getUniqueMappedResult();

        // Defaults
        int totalReservations = 0;
        Map<String, Integer> byStatus = new HashMap<>();
        List<Map<String, Object>> topEventTypes = new ArrayList<>();

        if (mapped != null) {
            List<TotalReservations> totalList = mapped.getTotalReservations();
            if (totalList != null && !totalList.isEmpty()) {
                Integer totalNum = totalList.get(0).getTotal();
                totalReservations = totalNum != null ? totalNum : 0;
            }

            List<StatusCount> statusList = mapped.getByStatus();
            if (statusList != null) {
                for (StatusCount s : statusList) {
                    String key = s.get_id() != null ? s.get_id() : "UNKNOWN";
                    byStatus.put(key, s.getCount() != null ? s.getCount() : 0);
                }
            }

            List<EventTypeCount> topList = mapped.getTopEventTypes();
            if (topList != null) {
                for (EventTypeCount t : topList) {
                    Map<String, Object> entry = new HashMap<>();
                    String key = t.get_id() != null ? t.get_id() : "UNKNOWN";
                    entry.put("eventType", key);
                    entry.put("count", t.getCount() != null ? t.getCount() : 0);
                    topEventTypes.add(entry);
                }
            }
        }

        reportData.put("totalReservations", totalReservations);
        reportData.put("byStatus", byStatus);
        reportData.put("topEventTypes", topEventTypes);

        return reportData;
    }

    // Genera reporte de ingresos
    private Map<String, Object> generateIncomeReport(ReportRequest request) {
        Map<String, Object> reportData = new HashMap<>();
        // Agrupa pagos por establecimiento en el período basado en la reserva asociada
        // Pipeline: lookup Reserva por reserve.id -> unwind reserve -> match reserve.dates dentro del periodo y status COMPLETADO
        Aggregation aggLookup = Aggregation.newAggregation(
                // lookup reserva
                Aggregation.lookup("Reserva", "reserve.id", "_id", "reserve"),
                Aggregation.unwind("$reserve", true),
                // match reserve.dates array contains any date in range and pagos COMPLETADO
                Aggregation.match(org.springframework.data.mongodb.core.query.Criteria.where("reserve.dates").gte(request.getFrom()).lte(request.getTo()).and("status").is("COMPLETADO"))
        );

        AggregationResults<Document> lookupResults = mongoTemplate.aggregate(aggLookup, "Pagos", Document.class);
        List<Document> docs = lookupResults.getMappedResults();

        double totalIncome = 0.0;
        List<Map<String, Object>> incomeByEstablishment = new ArrayList<>();

        // Map establishmentId -> {name, amount}
        Map<String, Double> acc = new HashMap<>();
        Map<String, String> nameMap = new HashMap<>();

        for (Document d : docs) {
            Object statusObj = d.get("status");
            if (statusObj == null) continue;
            String status = statusObj.toString();
            if (!"COMPLETADO".equalsIgnoreCase(status)) continue;

            Number cost = d.get("totalCost") instanceof Number ? (Number) d.get("totalCost") : null;
            double amt = cost != null ? cost.doubleValue() : 0.0;

            Object establishmentObj = d.get("establishment");
            if (establishmentObj instanceof Document) {
                Document establishment = (Document) establishmentObj;
                String estId = establishment.getString("id");
                String estName = establishment.getString("name");
                acc.put(estId, acc.getOrDefault(estId, 0.0) + amt);
                nameMap.putIfAbsent(estId, estName != null ? estName : "");
            }

            totalIncome += amt;
        }

        for (Map.Entry<String, Double> e : acc.entrySet()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("establishmentId", e.getKey());
            entry.put("establishmentName", nameMap.getOrDefault(e.getKey(), ""));
            entry.put("totalIncome", e.getValue());
            incomeByEstablishment.add(entry);
        }

        reportData.put("totalIncome", totalIncome);
        reportData.put("currency", "COP");
        reportData.put("incomeByEstablishment", incomeByEstablishment);
        reportData.put("paymentsCount", docs.size());

        return reportData;
    }

    // Genera reporte de servicios
    private Map<String, Object> generateServiceReport(ReportRequest request) {
        Map<String, Object> reportData = new HashMap<>();
        // Agregaciones sobre collection Pagos para extraer uso e ingresos por tipo de servicio
        Aggregation agg = Aggregation.newAggregation(
                // filter pagos asociados a reservas en el periodo via lookup
                Aggregation.lookup("Reserva", "reserve.id", "_id", "reserve"),
                Aggregation.unwind("$reserve", true),
                Aggregation.match(org.springframework.data.mongodb.core.query.Criteria.where("reserve.dates").gte(request.getFrom()).lte(request.getTo()))
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "Pagos", Document.class);
        List<Document> docs = results.getMappedResults();

        Map<String, Integer> usageCount = new HashMap<>();
        Map<String, Double> incomeByServiceType = new HashMap<>();

        for (Document d : docs) {
            Object covObj = d.get("coveredServices");
            if (!(covObj instanceof Document)) continue;
            Document covered = (Document) covObj;

            // Entertainment list
            Object entObj = covered.get("entertainment");
            if (entObj instanceof List<?>) {
                for (Object o : (List<?>) entObj) {
                    if (o instanceof Document) {
                        Document ent = (Document) o;
                        String svcName = ent.getString("name");
                        Number total = ent.get("totalCost") instanceof Number ? (Number) ent.get("totalCost") : null;
                        if (svcName != null) {
                            usageCount.put(svcName, usageCount.getOrDefault(svcName, 0) + 1);
                        }
                        incomeByServiceType.put("entertainment", incomeByServiceType.getOrDefault("entertainment", 0.0) + (total != null ? total.doubleValue() : 0.0));
                    }
                }
            }

            // Catering list
            Object catObj = covered.get("catering");
            if (catObj instanceof List<?>) {
                for (Object o : (List<?>) catObj) {
                    if (o instanceof Document) {
                        Document cat = (Document) o;
                        String desc = cat.getString("description");
                        Number total = cat.get("totalCost") instanceof Number ? (Number) cat.get("totalCost") : null;
                        if (desc != null) {
                            usageCount.put(desc, usageCount.getOrDefault(desc, 0) + 1);
                        }
                        incomeByServiceType.put("catering", incomeByServiceType.getOrDefault("catering", 0.0) + (total != null ? total.doubleValue() : 0.0));
                    }
                }
            }

            // Additional services
            Object addObj = covered.get("additionalServices");
            if (addObj instanceof List<?>) {
                for (Object o : (List<?>) addObj) {
                    if (o instanceof Document) {
                        Document add = (Document) o;
                        String name = add.getString("name");
                        Number cost = add.get("cost") instanceof Number ? (Number) add.get("cost") : null;
                        if (name != null) {
                            usageCount.put(name, usageCount.getOrDefault(name, 0) + 1);
                        }
                        incomeByServiceType.put("additional", incomeByServiceType.getOrDefault("additional", 0.0) + (cost != null ? cost.doubleValue() : 0.0));
                    }
                }
            }

            // Decoration (single)
            Object decObj = covered.get("decoration");
            if (decObj instanceof Document) {
                Document dec = (Document) decObj;
                Number cost = dec.get("cost") instanceof Number ? (Number) dec.get("cost") : null;
                incomeByServiceType.put("decoration", incomeByServiceType.getOrDefault("decoration", 0.0) + (cost != null ? cost.doubleValue() : 0.0));
            }
        }

        // Prepare mostUsedServices (top by usageCount)
        List<Map<String, Object>> mostUsedServices = usageCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("service", e.getKey());
                    m.put("count", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());

        // Prepare serviceIncome list
        List<Map<String, Object>> serviceIncome = incomeByServiceType.entrySet().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("serviceType", e.getKey());
                    m.put("totalIncome", e.getValue());
                    return m;
                })
                .collect(Collectors.toList());

        reportData.put("mostUsedServices", mostUsedServices);
        reportData.put("serviceIncome", serviceIncome);

        return reportData;
    }

    // Genera reporte de uso de establecimientos
    private Map<String, Object> generateEstablishmentUsageReport(ReportRequest request) {
        Map<String, Object> reportData = new HashMap<>();
        // Agregación sobre collection Reserva: match dates in period -> group by establishment.id
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(org.springframework.data.mongodb.core.query.Criteria.where("dates").gte(request.getFrom()).lte(request.getTo())),
                Aggregation.group("establishment.id").first("establishment.name").as("establishmentName").count().as("reservationsCount"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "reservationsCount"))
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "Reserva", Document.class);
        List<Document> docs = results.getMappedResults();

        List<Map<String, Object>> usageList = new ArrayList<>();
        Map<String, Object> mostUsed = null;
        for (Document d : docs) {
            Object idObj = d.getString("_id");
            String estId = idObj != null ? idObj.toString() : null;
            String estName = d.getString("establishmentName");
            Number cnt = d.get("reservationsCount") instanceof Number ? (Number) d.get("reservationsCount") : null;
            int count = cnt != null ? cnt.intValue() : 0;
            Map<String, Object> entry = new HashMap<>();
            entry.put("establishmentId", estId);
            entry.put("establishmentName", estName);
            entry.put("reservationsCount", count);
            usageList.add(entry);
            if (mostUsed == null) {
                mostUsed = entry;
            }
        }

        reportData.put("usageList", usageList);
        reportData.put("mostUsedEstablishment", mostUsed);

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
                .name(report.getName())
                .data(report.getData())
                .metadata(metadata)
                .build();
    }
}
