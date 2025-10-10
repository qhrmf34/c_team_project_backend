package com.hotel_project.hotel_jpa.city.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityHotelOneDto {
    private Long id;
    private String cityName;
    private String cityContent;
    private Long countryId;

    // 외래키 정보
    private String countryName;
    private String countryIdd;

    // 도시 이미지 정보
    private String cityImagePath;

    // 최저가 정보
    private Long minPrice;
}
