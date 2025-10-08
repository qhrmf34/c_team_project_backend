package com.hotel_project.hotel_jpa.hotel.dto;

import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesDto;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilterOptionsDto {
    private List<FreebiesDto> freebies;
    private List<AmenitiesDto> amenities;
    private PriceRangeDto priceRange;
    private List<String> hotelTypes;
    private Map<String, Long> hotelTypeCounts;
}