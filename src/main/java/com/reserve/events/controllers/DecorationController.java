package com.reserve.events.controllers;

import com.reserve.events.application.DecorationService;
import com.reserve.events.controllers.dto.DecorationRequest;
import com.reserve.events.controllers.response.DecorationResponse;
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
@RequestMapping("/decoration")
@Tag(name = "Controlador para los servicios de decoración")
@RequiredArgsConstructor
public class DecorationController {

    private final DecorationService decorationService;

    @PostMapping
    @Operation(summary = "Crear un nuevo servicio de decoración")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Servicio creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<DecorationResponse> createDecoration(@Valid @RequestBody DecorationRequest decorationRequest) {
        DecorationResponse response = decorationService.createDecoration(decorationRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los servicios de decoración")
    @ApiResponse(responseCode = "200", description = "Lista de decoraciones obtenida exitosamente")
    public ResponseEntity<List<DecorationResponse>> getAllDecoration() {
        return ResponseEntity.ok(decorationService.getAllDecoration());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un servicio de decoración por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicio encontrado"),
            @ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    public ResponseEntity<DecorationResponse> getDecorationById(@PathVariable String id) {
        return ResponseEntity.ok(decorationService.getDecorationById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un servicio de decoración existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicio actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    public ResponseEntity<DecorationResponse> updateDecoration(
            @PathVariable String id,
            @Valid @RequestBody DecorationRequest decorationRequest) {
        return ResponseEntity.ok(decorationService.updateDecoration(id, decorationRequest));
    }

}
