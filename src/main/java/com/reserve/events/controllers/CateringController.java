package com.reserve.events.controllers;

import com.reserve.events.application.CateringService;
import com.reserve.events.controllers.dto.CateringRequest;
import com.reserve.events.controllers.response.CateringResponse;
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
@RequestMapping("/catering")
@Tag(name = "Controlador para los servicios de caterin")
@RequiredArgsConstructor
public class CateringController {

    private final CateringService cateringService;

    @PostMapping
    @Operation(summary = "Crear un nuevo servicio de caterin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Servicio creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<CateringResponse> createCatering(@Valid @RequestBody CateringRequest cateringRequest) {
        CateringResponse response = cateringService.createCatering(cateringRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los servicios de catering")
    @ApiResponse(responseCode = "200", description = "Lista de servicios obtenida exitosamente")
    public ResponseEntity<List<CateringResponse>> getAllCatering() {
        return ResponseEntity.ok(cateringService.getAllCatering());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un servicio adicional por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicio encontrado"),
            @ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    public ResponseEntity<CateringResponse> getCateringById(@PathVariable String id) {
        return ResponseEntity.ok(cateringService.getCateringById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un servicio existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicio actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Servicio no encontrado")
    })
    public ResponseEntity<CateringResponse> updateCatering(
            @PathVariable String id,
            @Valid @RequestBody CateringRequest cateringRequest) {
        return ResponseEntity.ok(cateringService.updateCatering(id, cateringRequest));
    }
}
