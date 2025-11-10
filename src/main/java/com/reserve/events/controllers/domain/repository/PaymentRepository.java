package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {

    /**
     * Verifica si ya existe un pago para una reserva determinada.
     *
     * @param reserveId ID de la reserva
     * @return true si existe un pago asociado a esa reserva, false en caso contrario
     */
    boolean existsByReserve_Id(String reserveId);
}
