package com.hotel_project.hotel_jpa.city_image.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityImageViewDto {
    private Long id;
    private String cityImageName;
    private String cityImagePath;
    private Long cityImageSize;
    private Integer cityImageIndex;
    private LocalDateTime createdAt;
    private Long cityId;

    // 외래키 정보 (INNER JOIN으로 가져온 데이터)
    private String cityName;
    private String cityContent;
    private String countryName;
}