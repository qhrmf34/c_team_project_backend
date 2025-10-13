package com.hotel_project.hotel_jpa.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

// 객실 예약 가능 여부 및 가격 정보 DTO
// HotelThree.vue의 "잔여 객실" 섹션에서 사용
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomAvailabilityDto {
    private Long roomId;
    private String roomName;
    private String bedType;
    private BigDecimal basePrice;
    private BigDecimal totalPrice;
    private Integer nights;
    private Integer availableCount;  // 재고
    private String image;

    // Book Now용
    private Long hotelId;
    private String hotelName;
    private LocalDate checkIn;
    private LocalDate checkOut;
}