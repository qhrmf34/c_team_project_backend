package com.hotel_project.payment_jpa.payment_method.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)   // 모르는 필드 무시
public class TossBillingResponseDto {

    @JsonProperty("billingKey")
    private String billingKey;

    @JsonProperty("customerKey")
    private String customerKey;

    @JsonProperty("authenticatedAt")
    private String authenticatedAt;

    @JsonProperty("card")
    private CardInfo card;

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)   // <== 내부 클래스에도 필요!
    public static class CardInfo {
        @JsonProperty("company")
        private String company;

        @JsonProperty("number")
        private String number;

        @JsonProperty("cardType")
        private String cardType;

        @JsonProperty("ownerType")
        private String ownerType;
    }
}

