package com.hotel_project.hotel_jpa.hotel.dto;

import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesDto;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelSummaryDto implements IHotelSummary {
    private Long id;
    private String title;
    private String image;
    private Integer imageCount;
    private BigDecimal price;
    private String currency;
    private String location;
    private Integer stars;
    private String type;
    private HotelType hotelType;
    private Integer amenitiesCount;
    private BigDecimal rating;
    private String ratingText;
    private Integer reviewCount;
    private Boolean wishlisted;
    private String cityName;
    private List<FreebiesDto> freebies;
    private List<AmenitiesDto> amenities;
    private Boolean available;
}