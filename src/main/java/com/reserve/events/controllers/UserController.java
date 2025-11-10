package com.reserve.events.controllers;

import com.reserve.events.application.UserService;
import com.reserve.events.controllers.dto.LoginRequest;
import com.reserve.events.controllers.dto.UserRequest;
import com.reserve.events.controllers.response.UserCreatedResponse;
import com.reserve.events.controllers.response.UserLoginResponse;
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
@RequestMapping("/User")
@Tag(name = "usuarios", description = "Controlador para gestionar usuarios")
@RequiredArgsConstructor
public class UserController {

    // TODO: Crear UserService
    private final UserService userService;

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
    @Operation(summary = "Crear un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un usuario con el email proporcionado")
    })
    public ResponseEntity<UserLoginResponse> loginUser(@Valid @RequestBody LoginRequest userLoginRequest) {

        UserLoginResponse response = userService.loginUser(userLoginRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
