package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Reserve;
import com.reserve.events.controllers.domain.model.StatusReserve;
import com.reserve.events.controllers.domain.repository.PaymentRepository;
import com.reserve.events.controllers.domain.repository.ReserveRepository;
import com.reserve.events.controllers.domain.repository.UserRepository;
import com.reserve.events.controllers.dto.ReserveRequest;
import com.reserve.events.controllers.response.ReserveResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveService {

    private final ReserveRepository reserveRepository;

    private final PaymentRepository paymentRepository;

    public Reserve cancelarReserva(String id) {
        Reserve reserva = reserveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
        if (reserva.getStatus() == StatusReserve.CANCELADA) {
            throw new RuntimeException("La reserva ya está cancelada.");
        }
        // Actualizar estado
        reserva.setStatus(StatusReserve.CANCELADA);
        // Eliminar pago asociado
        paymentRepository.findById(id);
        return reserveRepository.save(reserva);
    }
}
