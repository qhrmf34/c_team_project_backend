package com.hotel_project.hotel_jpa.room_pricing.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomPricingViewDto {
    private Long id;
    private LocalDate date;
    private Long price;

    private Long roomId;
    private String roomName;
}