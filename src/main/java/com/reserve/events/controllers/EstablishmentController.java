package com.reserve.events.controllers;

import com.reserve.events.application.EstablishmentService;
import com.reserve.events.controllers.dto.EstablishmentRequest;
import com.reserve.events.controllers.response.EstablishmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/establishments")
@RequiredArgsConstructor
@Tag(name = "Establecimientos", description = "Controlador para gestionar los establecimientos de eventos")
public class EstablishmentController {

    private final EstablishmentService establishmentService;

    // Crear un nuevo establecimiento (POST)
    @PostMapping
    @Operation(summary = "Registrar un nuevo establecimiento", description = "Permite crear un establecimiento con todos sus datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Establecimiento creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Ya existe un establecimiento con el mismo nombre")
    })
    public ResponseEntity<EstablishmentResponse> createEstablishment(
            @Valid @RequestBody EstablishmentRequest request) {
        EstablishmentResponse response = establishmentService.createEstablishment(request);
        return ResponseEntity.ok(response);
    }

    // Obtener todos los establecimientos activos (GET)
    @GetMapping
    @Operation(summary = "Listar todos los establecimientos activos", description = "Devuelve una lista de establecimientos actualmente activos en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado obtenido exitosamente")
    })
    public ResponseEntity<List<EstablishmentResponse>> getAllActiveEstablishments() {
        List<EstablishmentResponse> response = establishmentService.getAllActiveEstablishments();
        return ResponseEntity.ok(response);
    }

    // Obtener un establecimiento por Id (GET)
    @GetMapping("/{id}")
    @Operation(summary = "Obtener un establecimiento por ID", description = "Busca un establecimiento activo mediante su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Establecimiento encontrado"),
            @ApiResponse(responseCode = "404", description = "Establecimiento no encontrado")
    })
    public ResponseEntity<EstablishmentResponse> getEstablishmentById(@PathVariable String id) {
        EstablishmentResponse response = establishmentService.getEstablishmentById(id);
        return ResponseEntity.ok(response);
    }

    // Actualización completa (PUT)
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar completamente un establecimiento", description = "Reemplaza todos los campos del establecimiento con los valores enviados en la solicitud.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Establecimiento actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Establecimiento no encontrado"),
            @ApiResponse(responseCode = "409", description = "Ya existe otro establecimiento con el mismo nombre")
    })
    public ResponseEntity<EstablishmentResponse> updateEstablishment(
            @PathVariable String id,
            @Valid @RequestBody EstablishmentRequest request) {
        EstablishmentResponse response = establishmentService.updateEstablishment(id, request);
        return ResponseEntity.ok(response);
    }

    // Actualización solo de unos campos (PATCH)
    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar parcialmente un establecimiento", description = "Permite modificar uno o varios campos de un establecimiento existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Establecimiento actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Establecimiento no encontrado"),
            @ApiResponse(responseCode = "409", description = "Ya existe otro establecimiento con el mismo nombre")
    })
    public ResponseEntity<EstablishmentResponse> patchEstablishment(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {
        EstablishmentResponse response = establishmentService.patchEstablishment(id, updates);
        return ResponseEntity.ok(response);
    }

    // Borrado lógico (DELETE)
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un establecimiento (borrado lógico)", description = "Desactiva el establecimiento sin eliminarlo físicamente de la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Establecimiento eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Establecimiento no encontrado")
    })
    public ResponseEntity<Void> deleteEstablishment(@PathVariable String id) {
        establishmentService.deleteEstablishment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/occupied-dates")
    @Operation(summary = "Obtener las fechas ocupadas de un establecimiento por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fechas obtenidas exitosamente"),
            @ApiResponse(responseCode = "404", description = "Establecimiento no encontrado")
    })
    public ResponseEntity<List<String>> getOccupiedDatesByEstablishmentId(@PathVariable String id) {
        List<LocalDate> datesOccupies = establishmentService.getOccupiedDatesByEstablishmentId(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return ResponseEntity.ok(
                datesOccupies.stream()
                        .map(date -> date.atStartOfDay().format(formatter)).toList()
        );
    }


}
