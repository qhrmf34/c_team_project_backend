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
    private Long availableCount;      // ✅ 예약 가능한 호텔만 (showing-count용)
    private Integer pageSize;
    private Map<String, Long> hotelTypeCounts;
}