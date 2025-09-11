package com.hotel_project.member_jpa.member_coupon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 회원-쿠폰 매핑 DTO
 * - 내 쿠폰 목록/상세 노출에 적합
 * - 쿠폰 자체의 요약정보(CouponInfo) 포함
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCouponDto implements IMemberCoupon {

    private Long id;

    @NotNull
    private Long memberId;

    @NotNull
    private Long couponId;

    @NotNull
    private Boolean isUsed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime usedAt;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /** 내 쿠폰 보기용 쿠폰 요약정보(옵션) */
    private CouponInfo coupon;

    /* ===================== 비즈니스 헬퍼 ===================== */

    /** 쿠폰 만료 여부 (coupon.lastDate 기준) */
    public boolean isExpired() {
        if (coupon == null || coupon.getLastDate() == null) return false; // 정보 없으면 만료 판단 보류
        return LocalDate.now().isAfter(coupon.getLastDate());
    }

    /** 지금 사용 가능한 상태인지(미사용 && 미만료 && 활성) */
    public boolean canUseNow() {
        if (Boolean.TRUE.equals(isUsed)) return false;
        if (coupon != null) {
            if (Boolean.FALSE.equals(coupon.getIsActive())) return false;
            if (coupon.getLastDate() != null && LocalDate.now().isAfter(coupon.getLastDate())) return false;
        }
        return true;
    }

    /** 사용 처리(성공 시 true) */
    public boolean markUsed() {
        if (!canUseNow()) return false;
        this.isUsed = true;
        this.usedAt = LocalDateTime.now();
        return true;
    }

    /* ===================== 변환 메서드 ===================== */

    /** 엔티티 → DTO (쿠폰 요약정보 미포함) */
    public static MemberCouponDto fromEntity(MemberCouponEntity e) {
        if (e == null) return null;
        return MemberCouponDto.builder()
                .id(e.getId())
                .memberId(e.getMemberId() != null ? e.getMemberId()
                        : (e.getMember() != null ? e.getMember().getId() : null))
                .couponId(e.getCouponId() != null ? e.getCouponId()
                        : (e.getCoupon() != null ? e.getCoupon().getId() : null))
                .isUsed(Boolean.TRUE.equals(e.getIsUsed()))
                .usedAt(e.getUsedAt())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    /** DTO → 엔티티 (연관 주입은 서비스에서 setMember/setCoupon로) */
    public MemberCouponEntity toEntity() {
        MemberCouponEntity e = new MemberCouponEntity();
        e.setId(this.id);
        e.setMemberId(this.memberId);   // 실제 저장 시에는 member 연관 주입 권장
        e.setCouponId(this.couponId);   // 실제 저장 시에는 coupon 연관 주입 권장
        e.setIsUsed(this.isUsed);
        e.setUsedAt(this.usedAt);
        e.setCreatedAt(this.createdAt);
        e.setUpdatedAt(this.updatedAt);
        return e;
    }

    /* ===================== IMemberCoupon 기본 구현 위임 ===================== */

    @Override public void copyMemberCoupon(IMemberCoupon src) { IMemberCoupon.super.copyMemberCoupon(src); }
    @Override public void copyNotNullMemberCoupon(IMemberCoupon src) { IMemberCoupon.super.copyNotNullMemberCoupon(src); }

    /* ===================== 내 쿠폰 보기용 내부 클래스 ===================== */

    /**
     * 쿠폰 요약정보(내 쿠폰 목록/상세에 함께 노출)
     * coupon_tbl 기반 필드만 최소 제공
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CouponInfo {
        private Long id;
        private String couponName;
        /** 할인율/금액: coupon_tbl.discount (엔티티 타입에 맞춰 BigDecimal 사용 권장) */
        private java.math.BigDecimal discount;
        /** 유효기간(마감일) */
        private LocalDate lastDate;
        /** 활성 여부 */
        private Boolean isActive;
        // 필요시 content 등 추가 가능: private String couponContent;
    }
}
