package com.reserve.events.controllers;

import com.reserve.events.controllers.dto.EventRequest;
import com.reserve.events.controllers.dto.EventResponse;
import com.reserve.events.application.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
@Tag(name = "Eventos", description = "Gestión de eventos")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un nuevo evento")
    public EventResponse createEvent(@Valid @RequestBody EventRequest request) {
        return eventService.createEvent(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar evento")
    public EventResponse updateEvent(@PathVariable String id, @Valid @RequestBody EventRequest request) {
        return eventService.updateEvent(id, request);
    }
}