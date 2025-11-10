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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
