// PaymentRequestDto.java
package com.hotel_project.payment_jpa.payments.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

    @NotNull(message = "예약 ID는 필수입니다")
    private Long reservationsId;

    @NotNull(message = "결제수단 ID는 필수입니다")
    private Long paymentMethodId;

    private Long couponId; // 쿠폰은 선택사항

    @NotNull(message = "결제 금액은 필수입니다")
    @Positive(message = "결제 금액은 0보다 커야 합니다")
    private BigDecimal paymentAmount;

    private String orderName;
    private String orderId;
    private String paymentType; // "full" 또는 "partial"
}