package com.hotel_project.member_jpa.member_coupon.dto;
import java.time.LocalDateTime;

public interface IMemberCoupon {
    Long getId();
    void setId(Long id);

    Long getMemberId();
    void setMemberId(Long memberId);

    Long getCouponId();
    void setCouponId(Long couponId);

    Boolean getIsUsed();
    void setIsUsed(Boolean isUsed);

    LocalDateTime getUsedAt();
    void setUsedAt(LocalDateTime usedAt);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    /* insert용: 모든 값 복사 */
    default void copyMemberCoupon(IMemberCoupon src) {
        setId(src.getId());
        setMemberId(src.getMemberId());
        setCouponId(src.getCouponId());
        setIsUsed(src.getIsUsed());
        setUsedAt(src.getUsedAt());
        setCreatedAt(src.getCreatedAt());
        setUpdatedAt(src.getUpdatedAt());
    }

    /* update용: null 아닌 값만 복사 */
    default void copyNotNullMemberCoupon(IMemberCoupon src) {
        if (src.getId() != null) setId(src.getId());
        if (src.getMemberId() != null) setMemberId(src.getMemberId());
        if (src.getCouponId() != null) setCouponId(src.getCouponId());
        if (src.getIsUsed() != null) setIsUsed(src.getIsUsed());
        if (src.getUsedAt() != null) setUsedAt(src.getUsedAt());
        if (src.getCreatedAt() != null) setCreatedAt(src.getCreatedAt());
        if (src.getUpdatedAt() != null) setUpdatedAt(src.getUpdatedAt());
    }
}