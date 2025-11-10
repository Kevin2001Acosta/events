package com.reserve.events.controllers.exception;

public class EstablishmentNotFoundException extends RuntimeException {
    public EstablishmentNotFoundException(String message) {
        super(message);
    }
}
