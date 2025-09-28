package com.hotel_project.hotel_jpa.hotel_freebies.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelFreebiesViewDto {
    private Long id;
    private Long hotelId;
    private Long freebiesId;
    private Boolean isAvailable;
    private LocalDateTime createdAt;

    // 외래키 정보 (INNER JOIN으로 가져온 데이터)
    private String hotelName;        // hotels.hotel_name
    private String freebiesName;    // amenities.amenities_name
}
