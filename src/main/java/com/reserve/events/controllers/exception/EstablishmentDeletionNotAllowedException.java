package com.reserve.events.controllers.exception;

public class EstablishmentDeletionNotAllowedException extends RuntimeException {
    public EstablishmentDeletionNotAllowedException(String message) {
        super(message);
    }
}

