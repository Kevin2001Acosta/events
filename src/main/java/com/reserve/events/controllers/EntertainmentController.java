package com.reserve.events.controllers;

import com.reserve.events.application.EntertainmentService;
import com.reserve.events.controllers.dto.EntertainmentRequest;
import com.reserve.events.controllers.response.EntertainmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/entertainment")
@Tag(name = "Controlador para los servicios de entretenimiento")
@RequiredArgsConstructor
public class EntertainmentController {

    private final EntertainmentService entertainmentService;

    @PostMapping
    @Operation(summary = "Crear un nuevo servicio de entretenimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Servicio creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un servicio de entretenimiento con el mismo nombre")
    })
    public ResponseEntity<EntertainmentResponse> createEntertainment(@Valid @RequestBody EntertainmentRequest entertainmentRequest) {
        EntertainmentResponse response = entertainmentService.createEntertainment(entertainmentRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los servicios de entretenimiento")
    @ApiResponse(responseCode = "200", description = "Lista de entretenimientos obtenida exitosamente")
    public ResponseEntity<List<EntertainmentResponse>> getAllEntertainment() {
        return ResponseEntity.ok(entertainmentService.getAllEntertainment());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un servicio de entretenimiento por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicio encontrado"),
            @ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    public ResponseEntity<EntertainmentResponse> getEntertainmentById(@PathVariable String id) {
        return ResponseEntity.ok(entertainmentService.getEntertainmentById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un servicio de entretenimiento existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicio actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    public ResponseEntity<EntertainmentResponse> updateEntertainment(
            @PathVariable String id,
            @Valid @RequestBody EntertainmentRequest entertainmentRequest) {
        return ResponseEntity.ok(entertainmentService.updateEntertainment(id, entertainmentRequest));
    }
}
