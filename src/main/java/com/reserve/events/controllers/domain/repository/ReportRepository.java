package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {

    // Encuentra reportes por tipo
    List<Report> findByType(String type);

    // Encuentra reportes dentro de un rango de fechas de creación
    List<Report> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    // Busca reportes que coincidan con un patrón (fútil para búsqueda de nombres)
    @Query("{ 'type': ?0 }")
    List<Report> findReportsByType(String type);
}
