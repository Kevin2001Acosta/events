package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Establishment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface EstablishmentRepository extends MongoRepository<Establishment, String> {

    //Verificar si ya existe un establecimiento activo con el mismo nombre
    boolean existsByNameAndActiveTrue(String name);

    //Retornar establecimientos activos
    List<Establishment> findByActiveTrue();

    //Buscar un establecimiento activo por Id (optional es porque podria no tener un valor)
    Optional<Establishment> findByIdAndActiveTrue(String id);
}
