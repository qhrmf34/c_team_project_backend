package com.hotel_project.payment_jpa.coupon.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponViewDto {
    private Long id;
    private String couponName;
    private BigDecimal discount;
    private LocalDate lastDate;
    private Boolean isActive;
}
