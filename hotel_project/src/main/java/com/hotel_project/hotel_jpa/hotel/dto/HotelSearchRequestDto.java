package com.hotel_project.hotel_jpa.hotel.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelSearchRequestDto {
    private String destination;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer guests;
    private Integer rooms;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer rating;
    private String hotelType;
    private List<Long> freebies;
    private List<Long> amenities;
    private String sortBy;
}