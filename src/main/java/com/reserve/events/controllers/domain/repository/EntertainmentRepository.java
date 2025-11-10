package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Entertainment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EntertainmentRepository extends MongoRepository<Entertainment, String> {

    /**
     * Busca un servicio de entretenimiento por su nombre
     * @param name Nombre del servicio de entretenimiento a buscar
     * @return Optional con el servicio si se encuentra
     */
    Optional<Entertainment> findByName(String name);

    /**
     * Verifica si existe un servicio de entretenimiento con el mismo nombre
     * @param name Nombre del servicio de entretenimiento a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByName(String name);
}
