package com.hotel_project.hotel_jpa.hotel.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceRangeDto {
    private BigDecimal min;
    private BigDecimal max;
}