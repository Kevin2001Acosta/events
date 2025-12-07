package com.reserve.events.controllers;

import com.reserve.events.controllers.dto.EventRequest;
import com.reserve.events.controllers.response.EventResponse;
import com.reserve.events.application.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Tag(name = "Eventos", description = "Gestión de eventos")
public class EventController {


    private final EventService eventService;

    @PostMapping
    @Operation(summary = "Crear un nuevo evento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un evento con este tipo")
    })
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.createEvent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar evento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
            @ApiResponse(responseCode = "409", description = "Ya existe un evento con este tipo")
    })
    public ResponseEntity<EventResponse> updateEvent(@PathVariable String id, @Valid @RequestBody EventRequest request) {
        EventResponse response = eventService.updateEvent(id, request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un evento (Solo administrador)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
            @ApiResponse(responseCode = "400", description = "No se puede eliminar porque tiene reservas activas")
    })
    public ResponseEntity<String> deleteEvent(@PathVariable String id) {
        String deletedId = eventService.deleteEvent(id);
        return ResponseEntity.ok("Evento con ID " + deletedId + " eliminado exitosamente.");
    }

    @GetMapping
    @Operation(summary = "Listar todos los eventos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos devuelta exitosamente")
    })
    public ResponseEntity<List<EventResponse>> listEvents() {
        List<EventResponse> events = eventService.listAllEvents();
        return ResponseEntity.ok(events);
    }
}