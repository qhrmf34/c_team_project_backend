package com.hotel_project.member_jpa.member_coupon.dto;

import com.hotel_project.member_jpa.member.dto.MemberEntity;
import com.hotel_project.payment_jpa.coupon.dto.CouponEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member_coupon_tbl")
public class MemberCouponEntity implements IMemberCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // PK 자동 증가

    /** FK 원시값 (읽기 전용) */
    @Column(name = "member_id", nullable = false, insertable = false, updatable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_member_coupon_member")
    )
    private MemberEntity member;

    /** FK 원시값 (읽기 전용) */
    @Column(name = "coupon_id", nullable = false, insertable = false, updatable = false)
    private Long couponId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "coupon_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_member_coupon_coupon")
    )
    private CouponEntity coupon;

    @Column(name = "is_used", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isUsed = false;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /* ===== IMemberCoupon 인터페이스 호환 ===== */

    @Override
    public Long getMemberId() {
        if (memberId != null) return memberId;
        return (member != null ? member.getId() : null);
    }

    @Override
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    @Override
    public Long getCouponId() {
        if (couponId != null) return couponId;
        return (coupon != null ? coupon.getId() : null);
    }

    @Override
    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }
}

