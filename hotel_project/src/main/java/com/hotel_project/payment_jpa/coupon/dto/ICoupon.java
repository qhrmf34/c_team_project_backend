package com.hotel_project.payment_jpa.coupon.dto;

import com.hotel_project.common_jpa.dto.IId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ICoupon extends IId {
    Long getId();
    void setId(Long id);

    String getCouponName();
    void setCouponName(String couponName);

    String getCouponContent();
    void setCouponContent(String couponContent);

    BigDecimal getDiscount();
    void setDiscount(BigDecimal discount);

    LocalDate getLastDate();
    void setLastDate(LocalDate lastDate);

    Boolean getIsActive();
    void setIsActive(Boolean isActive);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    /* insert용: 모든 값 복사 */
    default void copyMembers(ICoupon iCoupon) {
        setId(iCoupon.getId());
        setCouponName(iCoupon.getCouponName());
        setCouponContent(iCoupon.getCouponContent());
        setDiscount(iCoupon.getDiscount());
        setLastDate(iCoupon.getLastDate());
        setIsActive(iCoupon.getIsActive());
        setCreatedAt(iCoupon.getCreatedAt());
        setUpdatedAt(iCoupon.getUpdatedAt());
    }

    /* update용: null 아닌 값만 복사 */
    default void copyNotNullMembers(ICoupon iCoupon) {
        if (iCoupon.getId() != null) setId(iCoupon.getId());
        if (iCoupon.getCouponName() != null) setCouponName(iCoupon.getCouponName());
        if (iCoupon.getCouponContent() != null) setCouponContent(iCoupon.getCouponContent());
        if (iCoupon.getDiscount() != null) setDiscount(iCoupon.getDiscount());
        if (iCoupon.getLastDate() != null) setLastDate(iCoupon.getLastDate());
        if (iCoupon.getIsActive() != null) setIsActive(iCoupon.getIsActive());
        if (iCoupon.getUpdatedAt() != null) setUpdatedAt(iCoupon.getUpdatedAt());
    }
}
