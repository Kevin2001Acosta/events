package com.reserve.events.controllers.exception;

public class EventWithReservationsException extends RuntimeException {
    public EventWithReservationsException(String message) {
        super(message);
    }
}
