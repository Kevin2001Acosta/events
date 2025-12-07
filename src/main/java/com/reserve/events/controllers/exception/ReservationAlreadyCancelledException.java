package com.reserve.events.controllers.exception;

public class ReservationAlreadyCancelledException extends RuntimeException {
    public ReservationAlreadyCancelledException(String message) {
        super(message);
    }
}

