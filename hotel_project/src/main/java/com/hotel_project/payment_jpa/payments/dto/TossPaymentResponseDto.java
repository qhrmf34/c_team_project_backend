// TossPaymentResponseDto.java
package com.hotel_project.payment_jpa.payments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class TossPaymentResponseDto {

    @JsonProperty("paymentKey")
    private String paymentKey;

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("balanceAmount")
    private BigDecimal balanceAmount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("requestedAt")
    private String requestedAt;

    @JsonProperty("approvedAt")
    private String approvedAt;
}