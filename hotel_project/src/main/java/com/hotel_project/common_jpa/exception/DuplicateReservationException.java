package com.hotel_project.common_jpa.exception;

import com.hotel_project.payment_jpa.reservations.dto.ReservationsDto;
import lombok.Getter;

@Getter
public class DuplicateReservationException extends RuntimeException {
    private final int code = 409;
    private final ReservationsDto existingReservation;

    public DuplicateReservationException(String message, ReservationsDto existingReservation) {
        super(message);
        this.existingReservation = existingReservation;
    }
}