package com.hotel_project.hotel_jpa.hotel_amenities.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelAmenitiesDto implements IHotelAmenities{
    private Long id;

    private Long hotelId;

    private Long amenitiesId;

    @NotNull(message = "이용 가능 여부는 필수 입력입니다.")
    private Boolean isAvailable;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
