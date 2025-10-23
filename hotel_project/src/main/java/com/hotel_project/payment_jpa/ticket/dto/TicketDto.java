package com.hotel_project.payment_jpa.ticket.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.payment_jpa.payments.dto.PaymentsDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDto implements ITicket {
    private Long id;

    @JsonIgnore
    private PaymentsDto paymentsDto;

    @NotNull
    private Long paymentId;

    private String ticketImageName;

    // ✅ 바코드 추가
    private String barcode;

    @NotNull
    private Boolean isUsed;

    @NotNull
    private LocalDateTime createdAt;


    @Override
    public IId getPayments() {
        return this.paymentsDto;
    }

    @Override
    public void setPayments(IId iId) {
        if (iId == null) {
            return;
        }
        if (this.paymentsDto == null) {
            this.paymentsDto = new PaymentsDto();
        }
        this.paymentsDto.copyMembersId(iId);
    }

    @Override
    public Long getPaymentId() {
        if (this.paymentsDto != null) {
            return this.paymentsDto.getId();
        }
        return this.paymentId;
    }

    @Override
    public void setPaymentId(Long paymentId) {
        if (paymentId == null) {
            if (this.paymentsDto != null && this.paymentsDto.getId() != null) {
                this.paymentsDto.setId(this.paymentsDto.getId());
            }
            return;
        }
        this.paymentId = paymentId;
        if (this.paymentsDto == null) {
            this.paymentsDto = new PaymentsDto();
        }
        this.paymentsDto.setId(this.paymentId);
    }
}
