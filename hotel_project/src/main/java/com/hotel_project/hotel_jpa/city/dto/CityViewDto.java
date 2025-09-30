package com.hotel_project.hotel_jpa.city.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityViewDto {
    private Long id;
    private String cityName;
    private String cityContent;
    private Long countryId;

    // 외래키 정보 (INNER JOIN으로 가져온 데이터)
    private String countryName;
    private String countryIdd;
}