package com.hotel_project.payment_jpa.refund_reason.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundReasonDto implements IRefundReason {

    private Long id;

    @NotNull(message = "결제 ID는 필수입니다.")
    @JsonProperty("paymentId")
    private Long paymentId;

    @NotNull(message = "환불 사유는 필수입니다.")
    @JsonProperty("mainReason")
    private RefundReasonType mainReason;

    @Size(max = 1000, message = "상세 사유는 1000자 이하로 입력해주세요.")
    @JsonProperty("detailReason")
    private String detailReason;

    private LocalDateTime createdAt;
}