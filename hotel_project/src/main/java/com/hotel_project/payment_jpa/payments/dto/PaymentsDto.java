package com.hotel_project.payment_jpa.payments.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long reservationsId;

    @NotNull
    private ReservationsDto reservationsDto;

    @JsonIgnore
    private Long paymentMethodId;

    @NotNull
    private PaymentMethodDto paymentMethodDto;

    @JsonIgnore
    private Long couponId;

    private CouponDto couponDto;

    @NotNull(message = "최종 결제 금액은 필수 입력 입니다.")
    @Digits(integer = 10, fraction = 2, message = "최종 결제 금액은 최대 10자리 정수, 2자리 소수까지 가능합니다.")
    private BigDecimal paymentAmount;

    @NotNull(message = "결제 날짜는 필수 입력 입니다.")
    private LocalDateTime paymentDate;

    @NotNull(message = "결제 상태는 필수 입력입니다.")
    private PaymentStatus paymentStatus;

    @Size(max = 100, message = "토스 결제 키는 100자 이하로 입력해야합니다.")
    private String tossPaymentKey;

    @Digits(integer = 10, fraction = 2, message = "환불 금액은 최대 10자리 정수, 2자리 소수까지 가능합니다.")
    private BigDecimal refundAmount;

    private LocalDateTime refundDate;

    private LocalDateTime updatedAt;

    @Override
    public IId getReservations(){
        return this.reservationsDto;
    }

    @Override
    public void setReservations(IId iId) {
        if (iId == null){
            return;
        }
        if (this.reservationsDto == null){
            this.reservationsDto = new ReservationsDto();
        }
        this.reservationsDto.copyMembersId(iId);
    }

    @Override
    public Long getReservationsId(){
        if (this.reservationsDto != null){
            return this.reservationsDto.getId();
        }
        return this.reservationsId;
    }

    @Override
    public void setReservationsId(Long reservationsId) {
        if(reservationsId == null){
            if (this.reservationsDto != null && this.reservationsDto.getId() != null){
                this.reservationsDto.setId(this.reservationsDto.getId());
            }
            return;
        }
        this.reservationsId = reservationsId;
        if (this.reservationsDto == null){
            this.reservationsDto = new ReservationsDto();
        }
        this.reservationsDto.setId(reservationsId);
    }

    @Override
    public IId getPaymentMethod(){
        return this.paymentMethodDto;
    }

    @Override
    public void setPaymentMethod(IId paymentMethod) {
        if (paymentMethod == null){
            return;
        }
        if (this.paymentMethodDto == null){
            this.paymentMethodDto = new PaymentMethodDto();
        }
        this.paymentMethodDto.copyMembersId(paymentMethod);
    }

    @Override
    public Long getPaymentMethodId(){
        if (this.paymentMethodDto != null){
            return this.paymentMethodDto.getId();
        }
        return this.paymentMethodId;
    }

    @Override
    public void setPaymentMethodId(Long paymentMethodId) {
        if (paymentMethodId == null){
            if (this.paymentMethodDto != null && this.paymentMethodDto.getId() != null){
                this.paymentMethodDto.setId(this.paymentMethodDto.getId());
            }
            return;
        }
        this.paymentMethodId = paymentMethodId;
        if (this.paymentMethodDto == null){
            this.paymentMethodDto = new PaymentMethodDto();
        }
        this.paymentMethodDto.setId(paymentMethodId);
    }

    @Override
    public IId getCoupon(){
        return this.couponDto;
    }

    @Override
    public void setCoupon(IId coupon) {
        if (coupon == null){
            return;
        }
        if (this.couponDto == null){
            this.couponDto = new CouponDto();
        }
        this.couponDto.copyMembersId(coupon);
    }

    @Override
    public Long getCouponId(){
        if (this.couponDto != null){
            return this.couponDto.getId();
        }
        return this.couponId;
    }

    @Override
    public void setCouponId(Long couponId) {
        if (couponId == null){
            if (this.couponId != null && this.couponDto.getId() != null){
                this.couponDto.setId(this.couponDto.getId());
            }
            return;
        }
        this.couponId = couponId;
        if (this.couponDto == null){
            this.couponDto = new CouponDto();
        }
        this.couponDto.setId(couponId);
        this.couponId = couponId;
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
