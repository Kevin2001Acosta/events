package com.reserve.events.controllers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EstablishmentNotFoundException extends RuntimeException {
    public EstablishmentNotFoundException(String message) {
        super(message);
    }
}
