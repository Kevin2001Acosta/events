package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Establishment;
import com.reserve.events.controllers.domain.entity.Event;
import com.reserve.events.controllers.domain.entity.Reserve;
import com.reserve.events.controllers.domain.entity.User;
import com.reserve.events.controllers.domain.model.*;
import com.reserve.events.controllers.domain.repository.EstablishmentRepository;
import com.reserve.events.controllers.domain.repository.EventRepository;
import com.reserve.events.controllers.domain.repository.ReserveRepository;
import com.reserve.events.controllers.domain.repository.UserRepository;
import com.reserve.events.controllers.dto.ReserveRequest;
import com.reserve.events.controllers.exception.EstablishmentNotFoundException;
import com.reserve.events.controllers.exception.EventNotFoundException;
import com.reserve.events.controllers.exception.UserNotFoundException;
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
    private final EventRepository eventRepository;
    private final EstablishmentRepository establishmentRepository;
    private final UserRepository userRepository;

  /**  @Transactional
    public ReserveResponse createReserve(ReserveRequest request, String idClient){

        // Validar que el cliente exista
        User user = userRepository.findById(idClient)
                .orElseThrow(() -> new UserNotFoundException("No existe un usuario con el id: " + idClient));

        // Validar que el evento exista
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new EventNotFoundException("No existe un evento con el id: " + request.getEventId()));

        //Validar que el establecimiento exista
        Establishment establishment = establishmentRepository.findById(request.getEstablishmentId())
                .orElseThrow(() -> new EstablishmentNotFoundException("No existe un establecimiento con el id: " + request.getEstablishmentId()));


        // Crear los summary
        UserSummary userSummary = createUserSummary(user);
        EventSummary eventSummary = createEventSummary(event);
        EstablishmentSummary establishmentSummary = createEstablishmentSummary(establishment, request.getDates().size());

        // Establecer estado en PROGRAMADO
        StatusReserve status = StatusReserve.PROGRAMADA;

        // Mapear el ReserveRequest a la entidad Reserve
        Reserve reserve = Reserve.builder()
                .status(status)
                .guestNumber(request.getGuestNumber())
                .dates(request.getDates())
                .totalCost(1) // cambiar
                .comments(request.getComments())
                .client(userSummary)
                .event(eventSummary)
                .establishment(establishmentSummary)
                .build();

        return

    }
*/
    private UserSummary createUserSummary(User user) {
        return UserSummary.builder()
                .id(user.getId())
                .name(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    private EventSummary createEventSummary(Event event) {
        return EventSummary.builder()
                .id(event.getId())
                .type(event.getType())
                .build();
    }

    private EstablishmentSummary createEstablishmentSummary(Establishment establishment, Integer days) {
        double total = establishment.getCost() * days;
        return EstablishmentSummary.builder()
                .id(establishment.getId())
                .name(establishment.getName())
                .address(establishment.getAddress())
                .phone(establishment.getPhone())
                .totalCost(total)
                .build();
    }
}
