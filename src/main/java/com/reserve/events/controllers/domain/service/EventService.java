package com.reserve.events.controllers.domain.service;


import com.reserve.events.controllers.domain.entity.Event;
import com.reserve.events.controllers.domain.model.StatusReserve;
import com.reserve.events.controllers.domain.repository.EventRepository;
import com.reserve.events.controllers.domain.repository.ReserveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ReserveRepository reserveRepository;

    public String eliminarEvento(String id) {
        // Verificar que el evento exista
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Evento no encontrado con ID: " + id);
        }
        // Verificar si tiene reservas activas (con estado "PROGRAMADA")
        long reservasActivas = reserveRepository.countByEventIdAndStatus(id, StatusReserve.PROGRAMADA);
        if (reservasActivas > 0) {
            throw new RuntimeException("No se puede eliminar el evento porque tiene reservas activas asociadas.");
        }
        // Eliminar el evento
        eventRepository.deleteById(id);

        return id;
    }
    public List<Event> listarTodosLosEventos() {
        return eventRepository.findAll();
    }
}