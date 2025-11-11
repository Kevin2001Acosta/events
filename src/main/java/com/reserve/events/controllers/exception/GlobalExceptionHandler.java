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

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", "Bad Request");

        // Obtener todos los errores de validación
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "Ocurrió un error inesperado. Por favor, intente nuevamente más tarde.");
        body.put("path", request.getDescription(false).replace("uri=", ""));

        // Log the exception for debugging purposes
        log.error("Unhandled exception at {}", body.get("path"), ex);

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 409 CONFLICT: agrupar todas las 'AlreadyExists' y conflictos de disponibilidad
    @ExceptionHandler({
            ServiceAlreadyExistsException.class,
            AvailableEstablishmentNotFoundException.class,
            EventAlreadyExistsException.class,
            UserAlreadyExistsException.class,
            EstablishmentAlreadyExistsException.class,
            EstablishmentDeletionNotAllowedException.class,
            EventDeletionNotAllowedException.class,
            ReservationAlreadyCancelledException.class,
            ReservationCompletedCannotCancelException.class
    })
    public ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        return buildConflictResponse(request, ex.getMessage());
    }

    @ExceptionHandler({
            ForbiddenActionException.class
    })
    public ResponseEntity<Object> handleForbidden(RuntimeException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    // 404 NOT FOUND: agrupar todas las 'NotFound'
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

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Object> handleInvalidFormatException(InvalidFormatException ex, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");

        String fieldName = "";
        String invalidValue = "";
        String allowedValues = "";

        // Detectar si el targetType es un enum
        if (ex.getTargetType() != null && ex.getTargetType().isEnum()) {

            // Nombre del campo
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

        // Si la causa es InvalidFormatException
        if (cause instanceof InvalidFormatException) {
            return handleInvalidFormatException((InvalidFormatException) cause, request);
        }

        // Si la causa es otra cosa, devolver un error más genérico
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", "Error al procesar el cuerpo de la solicitud. Verifique el formato del JSON.");
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler({InvalidReservationDatesException.class, InvalidEstablishmentCapacityException.class})
    public ResponseEntity<Object> handleBadRequestValidation(RuntimeException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}