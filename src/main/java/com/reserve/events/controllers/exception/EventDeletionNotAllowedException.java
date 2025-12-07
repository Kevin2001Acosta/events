package com.reserve.events.controllers.exception;

public class EventDeletionNotAllowedException extends RuntimeException {
    public EventDeletionNotAllowedException(String message) {
        super(message);
    }
}

