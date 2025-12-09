package com.reserve.events.controllers;

import com.reserve.events.application.UserService;
import com.reserve.events.controllers.dto.LoginRequest;
import com.reserve.events.controllers.dto.UserRequest;
import com.reserve.events.controllers.response.UserCreatedResponse;
import com.reserve.events.controllers.response.UserLoginResponse;
import com.reserve.events.controllers.response.UserResponse;
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
@RequestMapping("/User")
@Tag(name = "usuarios", description = "Controlador para gestionar usuarios")
@RequiredArgsConstructor
public class UserController {

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
    @Operation(summary = "Logear un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario logeado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    public ResponseEntity<UserLoginResponse> loginUser(@Valid @RequestBody LoginRequest userLoginRequest) {

        UserLoginResponse response = userService.loginUser(userLoginRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Obtener lista de todos los usuarios")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un Usuario según su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Obtener lista de todos los usuarios por tipo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados"),
            @ApiResponse(responseCode = "404", description = "Usuarios no encontrados")
    })
    public ResponseEntity<List<UserResponse>> getUserByType(@PathVariable String type) {
        return ResponseEntity.ok(userService.getAllUsersByType(type));
    }

}
