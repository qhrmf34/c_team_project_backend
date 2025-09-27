package com.hotel_project.hotel_jpa.hotel.dto;

import lombok.*;

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
    private String hotelType;
    private Double hotelLatitude;
    private Double hotelLongitude;
    private String hotelContent;
    private Integer hotelStar;
    private String hotelNumber;
    private LocalTime checkinTime;
    private LocalTime checkoutTime;

    // 외래키 정보 (INNER JOIN으로 가져온 데이터)
    private String cityName;
}
