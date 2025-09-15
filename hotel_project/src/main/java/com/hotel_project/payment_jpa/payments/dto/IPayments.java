package com.hotel_project.payment_jpa.payments.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface IPayments extends IId {
    Long getId();
    void setId(Long id);

    IId getReservations();
    void setReservations(IId reservation);

    @JsonIgnore
    Long getReservationsId();
    void setReservationsId(Long reservationsId);

    IId getPaymentMethod();
    void setPaymentMethod(IId paymentMethod);

    @JsonIgnore
    Long getPaymentMethodId();
    void setPaymentMethodId(Long paymentMethodId);

    IId getCoupon();
    void setCoupon(IId coupon);

    @JsonIgnore
    Long getCouponId();
    void setCouponId(Long couponId);

    BigDecimal getPaymentAmount();
    void setPaymentAmount(BigDecimal paymentAmount);

    LocalDateTime getPaymentDate();
    void setPaymentDate(LocalDateTime paymentDate);

    PaymentStatus getPaymentStatus();
    void setPaymentStatus(PaymentStatus paymentStatus);

    String getTossPaymentKey();
    void setTossPaymentKey(String tossPaymentKey);

    BigDecimal getRefundAmount();
    void setRefundAmount(BigDecimal refundAmount);

    LocalDateTime getRefundDate();
    void setRefundDate(LocalDateTime refundDate);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updateAt);

    default void copyMembers(IPayments iPayments) {
        if (iPayments == null) {
            return;
        }
        setId(iPayments.getId());
        setReservations(iPayments.getReservations());
        setPaymentMethod(iPayments.getPaymentMethod());
        setCoupon(iPayments.getCoupon());
        setPaymentAmount(iPayments.getPaymentAmount());
        setPaymentDate(iPayments.getPaymentDate());
        setPaymentStatus(iPayments.getPaymentStatus());
        setTossPaymentKey(iPayments.getTossPaymentKey());
        setRefundAmount(iPayments.getRefundAmount());
        setRefundDate(iPayments.getRefundDate());
        setUpdatedAt(iPayments.getUpdatedAt());
    }

    default void copyNotNullMembers(IPayments iPayments) {
        if (iPayments == null) {
            return;
        }
        if (iPayments.getId() != null) { setId(iPayments.getId()); }
        if (iPayments.getReservations() != null) { setReservations(iPayments.getReservations()); }
        if (iPayments.getPaymentMethod() != null) { setPaymentMethod(iPayments.getPaymentMethod()); }
        if (iPayments.getCoupon() != null) { setCoupon(iPayments.getCoupon()); }
        if (iPayments.getPaymentAmount() != null) { setPaymentAmount(iPayments.getPaymentAmount()); }
        if (iPayments.getPaymentDate() != null) { setPaymentDate(iPayments.getPaymentDate()); }
        if (iPayments.getPaymentStatus() != null) { setPaymentStatus(iPayments.getPaymentStatus()); }
        if (iPayments.getTossPaymentKey() != null) { setTossPaymentKey(iPayments.getTossPaymentKey()); }
        if (iPayments.getRefundAmount() != null) { setRefundAmount(iPayments.getRefundAmount()); }
        if (iPayments.getRefundDate() != null) { setRefundDate(iPayments.getRefundDate()); }
        if (iPayments.getUpdatedAt() != null) { setUpdatedAt(iPayments.getUpdatedAt()); }
    }
}
