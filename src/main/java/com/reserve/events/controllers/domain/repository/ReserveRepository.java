package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Reserve;
import com.reserve.events.controllers.domain.model.StatusReserve;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import java.util.Optional;


public interface ReserveRepository extends MongoRepository<Reserve, String> {

    /**
     * Buscar una reserva por su id
     * @param id identificador de la reserva
     * @return la reserva si existe
     */
    Optional<Reserve> findById(@NonNull String id);

    /**
     * Cuenta las reservas por el id del evento y el estado
     * @param eventId identificador del evento
     * @param status estado de la reserva
     * @return cantidad de reservas que coinciden con los criterios
     */
    @Query(value = "{'event.id': ?0, 'status': ?1}", count = true)
    Long countByEventIdAndStatus(String eventId, StatusReserve status);

    /**
     * Encuentra todas las reservas de un cliente por su id
     * @param clientId id del cliente
     * @return lista de reservas
     */
    @Query("{'client.id': ?0}")
    java.util.List<Reserve> findByClientId(String clientId);

    /**
     * Encuentra una reserva por su id y el id del cliente (para validaciones de dueño)
     */
    @Query("{'id': ?0, 'client.id': ?1}")
    java.util.Optional<Reserve> findByIdAndClientId(String id, String clientId);
}
