package com.reserve.events.controllers;

import com.reserve.events.application.AdittionalService;
import com.reserve.events.controllers.dto.AdittionalRequest;
import com.reserve.events.controllers.response.AdittionalResponse;
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
@RequestMapping("/additional")
@Tag(name = "Controlador para los servicios adicionales")
@RequiredArgsConstructor
public class AdittionalController {

    private final AdittionalService adittionalService;

    @PostMapping
    @Operation(summary = "Crear un nuevo servicio adicional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Servicio creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un servicio adicional con el mismo nombre")
    })
    public ResponseEntity<AdittionalResponse> createAdittional(@Valid @RequestBody AdittionalRequest adittionalRequest){
        AdittionalResponse response = adittionalService.createAdittional(adittionalRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los servicios adicionales")
    @ApiResponse(responseCode = "200", description = "Lista de servicios obtenida exitosamente")
    public ResponseEntity<List<AdittionalResponse>> getAllAdittional() {
        return ResponseEntity.ok(adittionalService.getAllAdittional());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un servicio adicional por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicio encontrado"),
            @ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    public ResponseEntity<AdittionalResponse> getAdittionalById(@PathVariable String id) {
        return ResponseEntity.ok(adittionalService.getAdittionalById(id));
    }
}
