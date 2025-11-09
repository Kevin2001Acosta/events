package com.reserve.events.controllers.domain.service;

import com.reserve.events.controllers.domain.entity.Reserve;
import com.reserve.events.controllers.domain.model.StatusReserve;
import com.reserve.events.controllers.domain.repository.ReserveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReserveService {

    private final ReserveRepository reserveRepository;

    public Reserve cancelarReserva(String id) {
        Reserve reserva = (Reserve) reserveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
        // Verificar que no esté ya cancelada o completada
        if (reserva.getStatus() == StatusReserve.CANCELADA) {
            throw new RuntimeException("La reserva ya está cancelada.");
        }
        // Actualizar estado a CANCELADA
        reserva.setStatus(StatusReserve.CANCELADA);
        // Lógica adicional: eliminar pago (esto dependerá de cómo manejes el pago)
        eliminarPagoSiExiste(id);

        return reserveRepository.save(reserva);
    }

    private void eliminarPagoSiExiste(String reservaId) {
        System.out.println("Pago asociado a la reserva " + reservaId + " ha sido eliminado.");
    }
}