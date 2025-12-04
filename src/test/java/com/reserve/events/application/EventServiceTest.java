package com.reserve.events.application;

import com.reserve.events.controllers.domain.entity.Event;
import com.reserve.events.controllers.domain.repository.EventRepository;
import com.reserve.events.controllers.domain.repository.ReserveRepository;
import com.reserve.events.controllers.dto.EventRequest;
import com.reserve.events.controllers.exception.EventAlreadyExistsException;
import com.reserve.events.controllers.exception.ResourceConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ReserveRepository reserveRepository;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        // initialized by MockitoExtension
    }

    @Test
    void createEvent_whenTypeExists_throwsEventAlreadyExists() {
        EventRequest request = EventRequest.builder().type("Cumpleaños").build();

        when(eventRepository.existsByType("Cumpleaños")).thenReturn(true);

        assertThrows(EventAlreadyExistsException.class, () -> eventService.createEvent(request));

        verify(eventRepository, times(1)).existsByType("Cumpleaños");
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void deleteEvent_whenActiveReservations_throwResourceConflict() {
        String id = "evt-1";
        Event event = Event.builder().id(id).type("Tipo").imageUrl("/img").build();

        when(eventRepository.findById(id)).thenReturn(Optional.of(event));
        when(reserveRepository.countByEventIdAndStatus(id, com.reserve.events.controllers.domain.model.StatusReserve.PROGRAMADA))
                .thenReturn(1L);

        assertThrows(ResourceConflictException.class, () -> eventService.deleteEvent(id));

        verify(eventRepository, times(1)).findById(id);
        verify(reserveRepository, times(1)).countByEventIdAndStatus(id, com.reserve.events.controllers.domain.model.StatusReserve.PROGRAMADA);
        verify(eventRepository, never()).deleteById(id);
    }
}
