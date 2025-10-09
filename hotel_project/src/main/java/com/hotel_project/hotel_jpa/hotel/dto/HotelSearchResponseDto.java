package com.hotel_project.hotel_jpa.hotel.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelSearchResponseDto {
    private List<HotelSummaryDto> hotels;
    private Long totalCount;
    private Integer currentPage;
    private Integer totalPages;
    private Integer pageSize;
    private Map<String, Long> hotelTypeCounts;
}