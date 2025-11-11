package com.reserve.events.controllers.exception;

public class ReservationCompletedCannotCancelException extends RuntimeException {
    public ReservationCompletedCannotCancelException(String message) {
        super(message);
    }
}

