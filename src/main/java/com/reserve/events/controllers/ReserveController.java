package com.reserve.events.controllers;

import com.reserve.events.application.ReserveService;
import com.reserve.events.controllers.domain.entity.Reserve;
import com.reserve.events.controllers.dto.ReserveRequest;
import com.reserve.events.controllers.response.ReserveResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/reserve")
@Tag(name = "Controlador para las reservas")
@RequiredArgsConstructor
public class ReserveController {

    private final ReserveService reserveService;

    @PostMapping
    @Operation(summary = "Crear una nueva reserva")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<ReserveResponse> createReserve(@Valid @RequestBody ReserveRequest reserveRequest, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        ReserveResponse response = reserveService.createReserve(reserveRequest, email);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una reserva")
    public ResponseEntity<Reserve> cancelReservation(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String id) {
        Reserve reservaCancelada = reserveService.cancelarReserva(userDetails, id);
        return ResponseEntity.ok(reservaCancelada);
    }
}
