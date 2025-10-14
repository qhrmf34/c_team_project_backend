package com.hotel_project.payment_jpa.payment_method.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TossPaymentResponseDto {

    @JsonProperty("paymentKey")
    private String paymentKey;

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("orderName")
    private String orderName;

    @JsonProperty("status")
    private String status; // DONE, CANCELED, PARTIAL_CANCELED

    @JsonProperty("requestedAt")
    private String requestedAt;

    @JsonProperty("approvedAt")
    private String approvedAt;

    @JsonProperty("totalAmount")
    private Long totalAmount;

    @JsonProperty("balanceAmount")
    private Long balanceAmount;

    @JsonProperty("method")
    private String method; // 카드, 가상계좌, 계좌이체 등

    @JsonProperty("card")
    private CardInfo card;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CardInfo {
        @JsonProperty("company")
        private String company;

        @JsonProperty("number")
        private String number;

        @JsonProperty("approveNo")
        private String approveNo;
    }
}