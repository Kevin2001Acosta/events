package com.reserve.events.controllers;

import com.reserve.events.application.ReserveService;
import com.reserve.events.controllers.domain.entity.Reserve;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/reserve")
@Tag(name = "Controlador para las reservas")
@RequiredArgsConstructor
public class ReserveController {

    private final ReserveService reserveService;

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una reserva")
    public ResponseEntity<Reserve> cancelReservation(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String id) {
        Reserve reservaCancelada = reserveService.cancelarReserva(userDetails, id);
        return ResponseEntity.ok(reservaCancelada);
    }
}
