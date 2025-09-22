package com.hotel_project.payment_jpa.payments.dto;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.payment_jpa.coupon.dto.CouponEntity;
import com.hotel_project.payment_jpa.payment_method.dto.PaymentMethodEntity;
import com.hotel_project.payment_jpa.reservations.dto.ReservationsEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments_tbl")
public class PaymentsEntity implements IPayments{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservations_id",nullable = false)
    private ReservationsEntity reservationsEntity;

    @Transient
    private Long reservationsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethodEntity paymentMethodEntity;

    @Transient
    private Long paymentMethodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private CouponEntity couponEntity;

    @Transient
    private Long couponId;

    @Column(precision = 10, scale = 2,nullable = false)
    private BigDecimal paymentAmount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(length = 100)
    private String tossPaymentKey;

    @Column(precision = 10, scale = 2,nullable = false)
    private BigDecimal refundAmount;

    @Column(nullable = true,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime refundDate;

    @Column(nullable = true,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Override
    public IId getReservations(){
        return this.reservationsEntity;
    }

    @Override
    public void setReservations(IId iId){
        if(iId == null){
            return;
        }
        if(this.reservationsEntity == null){
            this.reservationsEntity = new ReservationsEntity();
        }
        this.reservationsEntity.copyMembersId(iId);
    }

    @Override
    public Long getReservationsId(){
        return this.reservationsEntity != null ? this.reservationsEntity.getId() : null;
    }

    @Override
    public void setReservationsId(Long reservationsId) {
        if(reservationsId == null){
            throw new IllegalArgumentException("reservationsId cannot be null");
        }
        if(this.reservationsEntity == null){
            this.reservationsEntity = new ReservationsEntity();
        }
        this.reservationsEntity.setId(reservationsId);
        this.reservationsId = reservationsId;
    }

    @Override
    public IId getPaymentMethod(){
        return this.paymentMethodEntity;
    }

    @Override
    public void setPaymentMethod(IId iId){
        if(iId == null){
            return;
        }
        if(this.paymentMethodEntity == null){
            this.paymentMethodEntity = new PaymentMethodEntity();
        }
        this.paymentMethodEntity.copyMembersId(iId);
    }

    @Override
    public Long getPaymentMethodId(){
        return this.paymentMethodEntity != null ? this.paymentMethodEntity.getId() : null;
    }

    @Override
    public void setPaymentMethodId(Long paymentMethodId) {
        if(paymentMethodId == null){
            throw new IllegalArgumentException("paymentMethodId cannot be null");
        }
        if(this.paymentMethodEntity == null){
            this.paymentMethodEntity = new PaymentMethodEntity();
        }
        this.paymentMethodEntity.setId(paymentMethodId);
        this.paymentMethodId = paymentMethodId;
    }

    @Override
    public IId getCoupon(){
        return this.couponEntity;
    }

    @Override
    public void setCoupon(IId iId){
        if(iId == null){
            return;
        }
        if(this.couponEntity == null){
            this.couponEntity = new CouponEntity();
        }
        this.couponEntity.copyMembersId(iId);
    }

    @Override
    public Long getCouponId(){
        return this.couponId != null ? this.couponId : null;
    }

    @Override
    public void setCouponId(Long couponId) {
        if(couponId == null){
            throw new IllegalArgumentException("couponId cannot be null");
        }
        if(this.couponEntity == null){
            this.couponEntity = new CouponEntity();
        }
        this.couponEntity.setId(couponId);
        this.couponId = couponId;
    }
}
