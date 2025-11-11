package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Reserve;
import com.reserve.events.controllers.domain.entity.User;
import com.reserve.events.controllers.domain.model.StatusReserve;
import com.reserve.events.controllers.domain.model.UserType;
import com.reserve.events.controllers.domain.repository.EstablishmentRepository;
import com.reserve.events.controllers.domain.repository.PaymentRepository;
import com.reserve.events.controllers.domain.repository.ReserveRepository;
import com.reserve.events.controllers.domain.repository.UserRepository;
import com.reserve.events.controllers.exception.UserNotFoundException;
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
    private final EstablishmentRepository establishmentRepository;

    @Transactional
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
        // eliminar Pago asociado a la reserva
        paymentRepository.deletePaymentByReserve_Id(reserva.getId());

        // Actualizar el estado de la reserva en las reservas del usuario
        updateReserveToCanceledInUser(reserva.getId(), user);

        // Buscar el establecimiento y actualizar su reserva
        updateReserveToCanceledInEstablishment(reserva.getEstablishment().getId(), reserva.getId());



        return reserveRepository.save(reserva);
    }

    private void updateReserveToCanceledInUser(String reservaId, User user) {
        // Actualizar el estado de la reserva en las reservas del usuario
        user.getEventBookings().stream()
                .filter(booking -> booking.getId().equals(reservaId)) // Buscar la reserva por ID
                .findFirst()
                .ifPresent(booking -> booking.setStatus(StatusReserve.CANCELADA)); // Actualizar el estado

        // Guardar los cambios en el usuario
        userRepository.save(user);
    }

    private void updateReserveToCanceledInEstablishment(String establishmentId, String reservaId){
        establishmentRepository.findById(establishmentId)
                .ifPresent(establishment ->{
                    establishment.getBookings().stream().filter(booking -> booking.getId().equals(reservaId))
                            .findFirst()
                            .ifPresent(booking -> booking.setStatus(StatusReserve.CANCELADA));
                    // Guarda los cambios en el establecimiento
                    establishmentRepository.save(establishment);
                });
    }
}
