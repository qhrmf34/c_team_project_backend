package com.hotel_project.hotel_jpa.hotel.dto;

import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesDto;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import com.hotel_project.hotel_jpa.room.dto.RoomDto;
import com.hotel_project.review_jpa.reviews.dto.ReviewsDto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDetailDto {
    private Long id;
    private String hotelName;
    private String description;
    private String address;
    private String cityName;
    private String countryName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String phoneNumber;
    private HotelType hotelType;
    private BigDecimal minPrice;
    private Integer starRating;
    private BigDecimal averageRating;
    private Integer reviewCount;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private List<String> images;
    private List<FreebiesDto> freebies;
    private List<AmenitiesDto> amenities;
    private List<RoomDto> rooms;
    private List<ReviewsDto> reviews;
    private Boolean wishlisted;
}