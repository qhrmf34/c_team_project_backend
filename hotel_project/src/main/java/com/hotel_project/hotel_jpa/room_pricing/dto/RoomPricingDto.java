package com.hotel_project.hotel_jpa.room_pricing.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomPricingDto implements IRoomPricing{
    private Long id;

    private Long roomId;

    @NotNull(message = "해당일은 필수 입력 입니다.")
    private LocalDate date;

    @NotNull(message = "가격은 필수 입력 입니다.")
    @Digits(integer = 10, fraction = 2, message = "가격은 최대 10자리 정수, 2자리 소수까지 가능합니다.")
    private BigDecimal price;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
