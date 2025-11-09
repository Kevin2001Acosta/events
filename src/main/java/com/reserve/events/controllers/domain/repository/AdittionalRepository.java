package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Adittional;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface AdittionalRepository extends MongoRepository <Adittional, String> {

    /**
     * Busca un servicio adicional por su nombre
     * @param name Nombre del servicio de entretenimiento a buscar
     * @return Optional con el servicio si se encuentra
     */
    Optional<Adittional> findByName(String name);

    /**
     * Verifica si existe un servicio adicional con el mismo nombre
     * @param name Nombre del servicio adicional a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existsByName(String name);
}
