package com.hotel_project.hotel_jpa.hotel_amenities.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelAmenitiesViewDto {
    private Long id;
    private Long hotelId;
    private Long amenitiesId;
    private LocalDateTime createdAt;

    // 외래키 정보 (INNER JOIN으로 가져온 데이터)
    private String hotelName;        // hotels.hotel_name
    private String amenitiesName;    // amenities.amenities_name
}
