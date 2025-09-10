package com.hotel_project.hotel_jpa.hotel_freebies.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelFreebiesDto implements IHotelFreebies {
    private Long id;

    private Long hotelId;

    private Long freebiesId;

    @NotNull(message = "이용 가능 여부는 필수 입력입니다.")
    private Boolean isAvailable;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
