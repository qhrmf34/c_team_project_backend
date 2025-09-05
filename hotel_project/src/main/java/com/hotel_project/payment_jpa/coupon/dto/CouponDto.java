package com.hotel_project.payment_jpa.coupon.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDto implements ICoupon{

    private Long id;

    @NotBlank(message = "쿠폰 이름은 필수 입력입니다.")
    @Size(min = 1, max = 100, message = "쿠폰 이름은 1자 이상 100자 이하로 입력해야 합니다.")
    private String couponName;

    @Size(max = 1000, message = "쿠폰 내용은 1000자 이하로 입력해야 합니다.")
    private String couponContent;

    @NotNull(message = "할인 금액은 필수 입력입니다.")
    @DecimalMin(value = "0.0", inclusive = false, message = "할인 금액은 0보다 커야 합니다.")
    @Digits(integer = 3, fraction = 2, message = "할인 금액은 최대 3자리 정수, 2자리 소수까지 가능합니다.")
    private BigDecimal discount;

    @NotNull(message = "마감일은 필수 입력입니다.")
    private LocalDate lastDate;

    @NotNull(message = "활성 여부는 필수 입력입니다.")
    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
