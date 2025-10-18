package com.hotel_project.common_jpa.controller.addvice;

import com.hotel_project.common_jpa.exception.DuplicateReservationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ReservationExceptionAdvice {

    @ExceptionHandler(DuplicateReservationException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateReservation(DuplicateReservationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", ex.getCode());
        response.put("message", ex.getMessage());
        response.put("reservationId", ex.getExistingReservation().getId());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}