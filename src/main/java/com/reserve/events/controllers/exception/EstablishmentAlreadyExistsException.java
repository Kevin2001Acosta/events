package com.reserve.events.controllers.exception;
public class EstablishmentAlreadyExistsException extends RuntimeException {
    public EstablishmentAlreadyExistsException(String message) {
        super(message);
    }
}