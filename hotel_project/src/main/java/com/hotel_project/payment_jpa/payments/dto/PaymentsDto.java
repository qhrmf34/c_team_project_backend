package com.hotel_project.payment_jpa.payments.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.payment_jpa.coupon.dto.CouponDto;
import com.hotel_project.payment_jpa.payment_method.dto.PaymentMethodDto;
import com.hotel_project.payment_jpa.reservations.dto.ReservationsDto;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentsDto implements IPayments {
    private Long id;

    @JsonIgnore
    private ReservationsDto reservationsDto;

    @NotNull(message = "예약 ID는 필수입니다.")
    @JsonProperty("reservationsId")  // ⭐ JSON에서 받기
    private Long reservationsId;

    @JsonIgnore
    private PaymentMethodDto paymentMethodDto;

    @NotNull(message = "결제수단 ID는 필수입니다.")
    @JsonProperty("paymentMethodId")  // ⭐ JSON에서 받기
    private Long paymentMethodId;

    @JsonIgnore
    private CouponDto couponDto;

    @JsonProperty("couponId")  // ⭐ JSON에서 받기 (선택사항)
    private Long couponId;

    @NotNull(message = "최종 결제 금액은 필수입니다.")
    private Long paymentAmount;

    @NotNull(message = "결제 날짜는 필수입니다.")
    private LocalDateTime paymentDate;

    @NotNull(message = "결제 상태는 필수입니다.")
    private PaymentStatus paymentStatus;

    @Size(max = 100, message = "토스 결제 키는 100자 이하로 입력해야합니다.")
    private String tossPaymentKey;

    @NotNull(message = "환불 여부는 필수입니다.")
    private Boolean refund;

    private LocalDateTime refundDate;
    private LocalDateTime updatedAt;

    // ===== IPayments 인터페이스 구현 =====

    @Override
    public IId getReservations() {
        return this.reservationsDto;
    }

    @Override
    public void setReservations(IId iId) {
        if (iId == null) return;
        if (this.reservationsDto == null) {
            this.reservationsDto = new ReservationsDto();
        }
        this.reservationsDto.copyMembersId(iId);
    }

    @Override
    @JsonIgnore  // ⭐ getter만 무시
    public Long getReservationsId() {
        if (this.reservationsDto != null && this.reservationsDto.getId() != null) {
            return this.reservationsDto.getId();
        }
        return this.reservationsId;
    }

    @Override
    public void setReservationsId(Long reservationsId) {
        this.reservationsId = reservationsId;
        if (reservationsId != null) {
            if (this.reservationsDto == null) {
                this.reservationsDto = new ReservationsDto();
            }
            this.reservationsDto.setId(reservationsId);
        }
    }

    @Override
    public IId getPaymentMethod() {
        return this.paymentMethodDto;
    }

    @Override
    public void setPaymentMethod(IId paymentMethod) {
        if (paymentMethod == null) return;
        if (this.paymentMethodDto == null) {
            this.paymentMethodDto = new PaymentMethodDto();
        }
        this.paymentMethodDto.copyMembersId(paymentMethod);
    }

    @Override
    @JsonIgnore  // ⭐ getter만 무시
    public Long getPaymentMethodId() {
        if (this.paymentMethodDto != null && this.paymentMethodDto.getId() != null) {
            return this.paymentMethodDto.getId();
        }
        return this.paymentMethodId;
    }

    @Override
    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
        if (paymentMethodId != null) {
            if (this.paymentMethodDto == null) {
                this.paymentMethodDto = new PaymentMethodDto();
            }
            this.paymentMethodDto.setId(paymentMethodId);
        }
    }

    @Override
    public IId getCoupon() {
        return this.couponDto;
    }

    @Override
    public void setCoupon(IId coupon) {
        if (coupon == null) return;
        if (this.couponDto == null) {
            this.couponDto = new CouponDto();
        }
        this.couponDto.copyMembersId(coupon);
    }

    @Override
    @JsonIgnore  // ⭐ getter만 무시
    public Long getCouponId() {
        if (this.couponDto != null && this.couponDto.getId() != null) {
            return this.couponDto.getId();
        }
        return this.couponId;
    }

    @Override
    public void setCouponId(Long couponId) {
        this.couponId = couponId;
        if (couponId != null) {
            if (this.couponDto == null) {
                this.couponDto = new CouponDto();
            }
            this.couponDto.setId(couponId);
        }
    }

    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (refundDate == null) {
            refundDate = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        refundDate = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
}