package com.reserve.events.controllers.domain.repository;

import com.reserve.events.controllers.domain.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {

    /**
     * Eliminar un pago por el Id de la reserva asociada
     * @param reserveId Id de la reserva
     * @return Optional con el pago eliminado si se encuentra
     */
    Optional<Payment> deletePaymentByReserve_Id(String reserveId);
    /**
     * Verifica si ya existe un pago para una reserva determinada.
     *
     * @param reserveId ID de la reserva
     * @return true si existe un pago asociado a esa reserva, false en caso contrario
     */
    boolean existsByReserve_Id(String reserveId);
}
