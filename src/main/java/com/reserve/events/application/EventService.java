package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Event;
import com.reserve.events.controllers.domain.repository.EventRepository;
import com.reserve.events.controllers.dto.EventRequest;
import com.reserve.events.controllers.dto.EventResponse;
import com.reserve.events.controllers.exception.EventAlreadyExistsException;
import com.reserve.events.controllers.exception.EventNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    // Imágenes predefinidas por tipo
    private final Map<String, String> predefinedEventImages = Map.of(
            "Cumpleaños", "/cumpleaños.jpg",
            "Bodas", "/bodas.jpg",
            "Grados", "/grados.jpg",
            "Bautizos", "/bautizos.jpg",
            "Fiesta infantil", "/infantil.jpg",
            "Baby shower", "/baby.jpg",
            "Fiesta de 15 años", "/fiesta15.jpg",
            "Despedida de solter@", "/despedida.jpg",
            "Conferencias y capacitaciones", "/conferencias.jpg",
            "Reunión corporativa", "/corporativa.jpg"
    );

    private final String DEFAULT_IMAGE = "/default.jpg";

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventResponse createEvent(EventRequest request) {

        // Verificar que no exista un evento con mismo type
        if (eventRepository.existsByType(request.getType())) {
            throw new EventAlreadyExistsException("Ya existe un evento con el tipo: " + request.getType());
        }

        // Asignar automáticamente la imagen
        String imageUrl = assignEventImage(request.getType());

        // Guardar con type, description, imageUrl
        Event event = Event.builder()
                .type(request.getType())
                .imageUrl(imageUrl)
                .build();

        Event savedEvent = eventRepository.save(event);
        return mapToEventResponse(savedEvent);
    }

    // ACTUALIZAR EVENTO
    public EventResponse updateEvent(String id, EventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Evento no encontrado con id: " + id));

        // Verificar duplicados al actualizar
        if (!event.getType().equals(request.getType()) &&
                eventRepository.existsByType(request.getType())) {
            throw new EventAlreadyExistsException("Ya existe un evento con el tipo: " + request.getType());
        }

        // Re-asignar imagen si cambió el type
        String imageUrl = event.getType().equals(request.getType())
                ? event.getImageUrl()
                : assignEventImage(request.getType());

        event.setType(request.getType());
        event.setImageUrl(imageUrl);

        Event updatedEvent = eventRepository.save(event);
        return mapToEventResponse(updatedEvent);
    }

    //  Método para asignar imagen automáticamente
    private String assignEventImage(String eventType) {
        return predefinedEventImages.getOrDefault(eventType, DEFAULT_IMAGE);
    }

    // Mapear Entity a Response
    private EventResponse mapToEventResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .type(event.getType())
                .imageUrl(event.getImageUrl())
                .build();
    }
}