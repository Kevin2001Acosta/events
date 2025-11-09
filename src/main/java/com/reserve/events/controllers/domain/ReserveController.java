package com.reserve.events.controllers.domain;

import com.reserve.events.controllers.domain.entity.Reserve;
import com.reserve.events.controllers.domain.service.ReserveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Operaciones relacionadas con las reservas")
public class ReserveController {

    private final ReserveService reserveService;

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar una reserva")
    public ResponseEntity<Reserve> cancelarReserva(@PathVariable String id) {
        Reserve reservaCancelada = reserveService.cancelarReserva(id);
        return ResponseEntity.ok(reservaCancelada);
    }
}
