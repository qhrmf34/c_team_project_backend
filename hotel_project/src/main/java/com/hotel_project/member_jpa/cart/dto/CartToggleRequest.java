package com.hotel_project.member_jpa.cart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartToggleRequest {

    @NotNull(message = "호텔 ID는 필수입니다.")
    private Long hotelId;
}