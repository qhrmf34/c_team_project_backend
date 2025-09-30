package com.hotel_project.hotel_jpa.room.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomViewDto {
    private Long id;
    private String roomName;
    private Integer roomNumber;
    private BigDecimal basePrice;
    private Byte roomSingleBed;
    private Byte roomDoubleBed;
    private String roomView;

    // 외래키 정보 (INNER JOIN으로 가져온 데이터)
    private String hotelName;
    private String cityName;
}