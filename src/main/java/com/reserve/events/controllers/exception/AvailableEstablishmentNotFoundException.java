package com.reserve.events.controllers.exception;

public class AvailableEstablishmentNotFoundException extends RuntimeException {
    public AvailableEstablishmentNotFoundException(String message) {
        super(message);
    }
}
