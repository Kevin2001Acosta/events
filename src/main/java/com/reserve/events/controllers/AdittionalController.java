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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
