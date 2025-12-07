package com.reserve.events.controllers.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ==================== MÉTODOS AUXILIARES ====================

    private ResponseEntity<Object> buildBadRequestResponse(WebRequest request, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> buildNotFoundResponse(WebRequest request, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<Object> buildConflictResponse(WebRequest request, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", "Conflict");
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    private ResponseEntity<Object> buildForbiddenResponse(WebRequest request, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    // ==================== 400 BAD REQUEST ====================
    // Agrupar todas las excepciones de validación y formato inválido

    @ExceptionHandler({
            InvalidReservationDatesException.class,
            InvalidEstablishmentCapacityException.class,
            BadRequestException.class
    })
    public ResponseEntity<Object> handleBadRequestValidation(RuntimeException ex, WebRequest request) {
        return buildBadRequestResponse(request, ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", "Bad Request");

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ?
                                fieldError.getDefaultMessage() : "Error de validación"
                ));

        body.put("errors", errors);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");

        String fieldName = "";
        String invalidValue = "";
        String allowedValues = "";

        if (ex.getTargetType() != null && ex.getTargetType().isEnum()) {
            if (!ex.getPath().isEmpty()) {
                fieldName = ex.getPath().get(0).getFieldName();
            }
            invalidValue = ex.getValue() != null ? ex.getValue().toString() : "null";

            allowedValues = Arrays.stream(ex.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
        }

        String message = String.format(
                "El valor '%s' no es válido para el campo '%s'. Valores permitidos: %s",
                invalidValue, fieldName, allowedValues
        );

        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException) {
            return handleInvalidFormatException((InvalidFormatException) cause, request);
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Error al procesar el cuerpo de la solicitud. Verifique el formato del JSON.");
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // ==================== 403 FORBIDDEN ====================
    // Agrupar todas las excepciones de acceso denegado

    @ExceptionHandler({
            ForbiddenActionException.class,
            ForbiddenException.class
    })
    public ResponseEntity<Object> handleForbidden(RuntimeException ex, WebRequest request) {
        return buildForbiddenResponse(request, ex.getMessage());
    }

    // ==================== 404 NOT FOUND ====================
    // Agrupar todas las excepciones de recursos no encontrados

    @ExceptionHandler({
            EventNotFoundException.class,
            EstablishmentNotFoundException.class,
            PaymentNotFoundException.class,
            ReserveNotFoundException.class,
            ServiceNotFoundException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        return buildNotFoundResponse(request, ex.getMessage());
    }

    // ==================== 409 CONFLICT ====================
    // Agrupar todas las excepciones de conflictos (duplicados, estados inválidos)

    @ExceptionHandler({
            ServiceAlreadyExistsException.class,
            AvailableEstablishmentNotFoundException.class,
            EventAlreadyExistsException.class,
            UserAlreadyExistsException.class,
            EstablishmentAlreadyExistsException.class,
            EstablishmentDeletionNotAllowedException.class,
            EventDeletionNotAllowedException.class,
            ReservationAlreadyCancelledException.class,
            EstablishmentWithReservationsException.class,
            EventWithReservationsException.class,
            ReservationCompletedCannotCancelException.class,
            ResourceConflictException.class
    })
    public ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        return buildConflictResponse(request, ex.getMessage());
    }

    // ==================== 500 INTERNAL SERVER ERROR ====================
    // Capturar cualquier excepción no manejada

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "Ocurrió un error inesperado. Por favor, intente nuevamente más tarde.");
        body.put("path", request.getDescription(false).replace("uri=", ""));

        log.error("Unhandled exception at {}", body.get("path"), ex);

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
