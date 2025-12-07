package com.reserve.events.controllers.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidReservationDatesException extends RuntimeException {
    public InvalidReservationDatesException(String message) {
        super(message);
    }
}

