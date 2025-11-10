package com.reserve.events.controllers;

import com.reserve.events.application.EstablishmentService;
import com.reserve.events.controllers.dto.EstablishmentRequest;
import com.reserve.events.controllers.response.EstablishmentResponse;
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
@RequestMapping("/establishments")
@Tag(name = "establecimientos", description = "Controlador para gestionar establecimientos de eventos")
@RequiredArgsConstructor
public class EstablishmentController {

    private final EstablishmentService establishmentService;


    // Crear un nuevo establecimiento - ADMIN
    @PostMapping
    @Operation(summary = "Registrar un nuevo establecimiento")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Establecimiento creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un establecimiento con el mismo nombre")
    })
    public ResponseEntity<EstablishmentResponse> createEstablishment(
            @Valid @RequestBody EstablishmentRequest request) {

        EstablishmentResponse response = establishmentService.createEstablishment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    // Listar todos los establecimientos activos
    @GetMapping
    @Operation(summary = "Listar todos los establecimientos activos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    })
    public ResponseEntity<List<EstablishmentResponse>> getAllActiveEstablishments(
            @RequestParam(name = "active", required = false, defaultValue = "true") boolean active) {

        List<EstablishmentResponse> response;
        if (active) {
            response = establishmentService.getAllActiveEstablishments();
        } else {
            // Si en el futuro agregamos getAllEstablishments incluyendo inactivos
            response = establishmentService.getAllActiveEstablishments(); // placeholder
        }
        return ResponseEntity.ok(response);
    }

    // Obtener información de un establecimiento por Id
    @GetMapping("/{id}")
    @Operation(summary = "Obtener información de un establecimiento por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Establecimiento encontrado"),
            @ApiResponse(responseCode = "404", description = "Establecimiento no encontrado")
    })
    public ResponseEntity<EstablishmentResponse> getEstablishmentById(@PathVariable String id) {
        EstablishmentResponse response = establishmentService.getEstablishmentById(id);
        return ResponseEntity.ok(response);
    }


    // Actualizar un establecimiento existente - ADMIN
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un establecimiento por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Establecimiento actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Establecimiento no encontrado"),
            @ApiResponse(responseCode = "409", description = "Ya existe otro establecimiento con el mismo nombre")
    })
    public ResponseEntity<EstablishmentResponse> updateEstablishment(
            @PathVariable String id,
            @Valid @RequestBody EstablishmentRequest request) {

        EstablishmentResponse response = establishmentService.updateEstablishment(id, request);
        return ResponseEntity.ok(response);
    }

    // Borrando logico de un establecimiento - ADMIN
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar (borrado lógico) un establecimiento por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Establecimiento eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Establecimiento no encontrado")
    })
    public ResponseEntity<Void> deleteEstablishment(@PathVariable String id) {
        establishmentService.deleteEstablishment(id);
        return ResponseEntity.noContent().build();
    }
}
