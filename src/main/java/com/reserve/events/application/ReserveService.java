package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Reserve;
import com.reserve.events.controllers.domain.entity.User;
import com.reserve.events.controllers.domain.model.StatusReserve;
import com.reserve.events.controllers.domain.model.UserType;
import com.reserve.events.controllers.domain.repository.PaymentRepository;
import com.reserve.events.controllers.domain.repository.ReserveRepository;
import com.reserve.events.controllers.domain.repository.UserRepository;
import com.reserve.events.controllers.dto.ReserveRequest;
import com.reserve.events.controllers.exception.UserNotFoundException;
import com.reserve.events.controllers.response.ReserveResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveService {

    private final ReserveRepository reserveRepository;
    private final UserRepository userRepository;

    private final PaymentRepository paymentRepository;


    public Reserve cancelarReserva(UserDetails userDetails, String id) {
        Reserve reserva = reserveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

        // traer al usuario logueado
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con email: " + userDetails.getUsername()));

        // verifica que si el usuario es cliente, sea el dueño de la reserva a cancelar
        if(!user.getType().equals(UserType.CLIENTE) || !reserva.getClient().getId().equals(user.getId())) {
            throw new RuntimeException("Como CLIENTE no puedes cancelar una reserva que no es tuya.");
        }

        // Verificar que no esté ya cancelada o completada
        if (reserva.getStatus() == StatusReserve.CANCELADA) {
            throw new RuntimeException("La reserva ya está cancelada.");
        }
        if (reserva.getStatus() == StatusReserve.COMPLETADA) {
            throw new RuntimeException("No se puede cancelar una reserva completada.");
        }

        // Actualizar estado a CANCELADA
        reserva.setStatus(StatusReserve.CANCELADA);
        // TODO: Lógica adicional: eliminar pago (esto dependerá de cómo manejes el pago)
        paymentRepository.deleteByReserveId(reserva.getId());

        return reserveRepository.save(reserva);
    }
}
