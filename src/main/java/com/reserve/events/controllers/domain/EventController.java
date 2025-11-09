package com.reserve.events.controllers.domain;

import com.reserve.events.controllers.domain.entity.Event;
import com.reserve.events.controllers.domain.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
@Tag(name = "Eventos", description = "Operaciones relacionadas con los eventos")
public class EventController {

    private final EventService eventService;

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un evento (Solo administrador)")
    public ResponseEntity<String> eliminarEvento(@PathVariable String id) {
        String eventoId = eventService.eliminarEvento(id);
        return ResponseEntity.ok("Evento con ID " + eventoId + " eliminado exitosamente.");
    }

    @GetMapping
    @Operation(summary = "Listar todos los eventos")
    public ResponseEntity<List<Event>> listarEventos() {
        List<Event> eventos = eventService.listarTodosLosEventos();
        return ResponseEntity.ok(eventos);
    }
}
