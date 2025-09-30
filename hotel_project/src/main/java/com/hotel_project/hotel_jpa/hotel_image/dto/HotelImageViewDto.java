package com.hotel_project.hotel_jpa.hotel_image.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelImageViewDto {
    private Long id;
    private String hotelImageName;
    private String hotelImagePath;
    private Integer hotelImageIndex;
    private LocalDateTime createdAt;

    // 외래키 정보 (INNER JOIN으로 가져온 데이터)
    private Long hotelId;
    private String hotelName;

    // 추가적인 호텔 정보 (필요시)
    private String cityName;
    private String countryName;
}