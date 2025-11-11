package com.reserve.events.controllers.exception;

public class ReserveNotFoundException extends RuntimeException {
    public ReserveNotFoundException(String message) {
        super(message);
    }
}
