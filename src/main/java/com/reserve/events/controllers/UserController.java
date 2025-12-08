package com.reserve.events.controllers;

import com.reserve.events.application.UserService;
import com.reserve.events.application.ReserveService;
import com.reserve.events.controllers.dto.LoginRequest;
import com.reserve.events.controllers.dto.UserRequest;
import com.reserve.events.controllers.response.UserCreatedResponse;
import com.reserve.events.controllers.response.UserLoginResponse;
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

import java.util.List;

@Validated
@RestController
@RequestMapping("/User")
@Tag(name = "usuarios", description = "Controlador para gestionar usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ReserveService reserveService;

    @PostMapping("/register")
    @Operation(summary = "Crear un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un usuario con el email proporcionado")
    })
    public ResponseEntity<UserCreatedResponse> createUser(@Valid @RequestBody UserRequest userRequest) {

        UserCreatedResponse response = userService.createUser(userRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Logear un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario logeado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Credenciales incorrectas")
    })
    public ResponseEntity<UserLoginResponse> loginUser(@Valid @RequestBody LoginRequest userLoginRequest) {

        UserLoginResponse response = userService.loginUser(userLoginRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/reserves")
    @Operation(summary = "Obtener las reservas del usuario autenticado (desde user.eventBookings)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas obtenidas exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<List<ReserveResponse>> getUserReserves(@AuthenticationPrincipal UserDetails userDetails) {
        List<ReserveResponse> reserves = reserveService.listReservesByUserEmail(userDetails.getUsername());
        return ResponseEntity.ok(reserves);
    }

}
