package com.reserve.events.controllers;

import com.reserve.events.application.DecorationService;
import com.reserve.events.controllers.dto.DecorationRequest;
import com.reserve.events.controllers.response.DecorationResponse;
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
}
