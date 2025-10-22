package com.hotel_project.payment_jpa.reservations.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

// ReservationHistoryDto.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationHistoryDto {
    private Long reservationId;
    private Long roomId;
    private Long hotelId;
    private String hotelName;
    private String roomName;
    private String cityName;
    private String countryName;
    private String hotelAddress;
    private String hotelImage;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer guestsCount;
    private BigDecimal basePayment;
    private Boolean reservationsStatus;

    private String paymentStatus;
    private Long paymentId;
    private Boolean refund;
    private Integer hotelStar;
    private String hotelType;
    private BigDecimal hotelRating;
    private Integer reviewCount;
    private String roomNumber;
}