package com.hotel_project.payment_jpa.member_coupon.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;

import java.time.LocalDateTime;

public interface IMemberCoupon extends IId {
    Long getId();
    void setId(Long id);

    Long getMemberId();
    void setMemberId(Long memberId);

    @JsonIgnore
    IId getMember();
    void setMember(IId member);

    Long getCouponId();
    void setCouponId(Long couponId);

    @JsonIgnore
    IId getCoupon();
    void setCoupon(IId coupon);

    Boolean getIsUsed();
    void setIsUsed(Boolean isUsed);

    LocalDateTime getUsedAt();
    void setUsedAt(LocalDateTime usedAt);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    default void copyMembers(IMemberCoupon iMemberCoupon) {
        if (iMemberCoupon == null) { return; }
        setId(iMemberCoupon.getId());
        setMember(iMemberCoupon.getMember());
        setCoupon(iMemberCoupon.getCoupon());
        setIsUsed(iMemberCoupon.getIsUsed());
        setUsedAt(iMemberCoupon.getUsedAt());
        setCreatedAt(iMemberCoupon.getCreatedAt());
        setUpdatedAt(iMemberCoupon.getUpdatedAt());
    }

    default void copyNotNullMembers(IMemberCoupon iMemberCoupon) {
        if (iMemberCoupon == null) { return;}
        if (iMemberCoupon.getId() != null) { setId(iMemberCoupon.getId()); }
        if (iMemberCoupon.getMember() != null) { setMember(iMemberCoupon.getMember()); }
        if (iMemberCoupon.getCoupon() != null) { setCoupon(iMemberCoupon.getCoupon()); }
        if (iMemberCoupon.getIsUsed() != null) { setIsUsed(iMemberCoupon.getIsUsed()); }
        if (iMemberCoupon.getUsedAt() != null) { setUsedAt(iMemberCoupon.getUsedAt()); }
        if (iMemberCoupon.getUpdatedAt() != null) { setUpdatedAt(iMemberCoupon.getUpdatedAt()); }
    }
}
