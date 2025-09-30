package com.hotel_project.hotel_jpa.hotel.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelViewDto {
    private Long id;
    private String hotelName;
    private Long cityId;
    private HotelType hotelType;
    private BigDecimal hotelLatitude;
    private BigDecimal hotelLongitude;
    private String hotelContent;
    private Integer hotelStar;
    private String hotelNumber;
    private LocalTime checkinTime;
    private LocalTime checkoutTime;
    private LocalDateTime createdAt;

    // 외래키 정보
    private String cityName;
}