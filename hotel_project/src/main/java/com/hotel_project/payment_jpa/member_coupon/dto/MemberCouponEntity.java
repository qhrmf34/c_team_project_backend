package com.hotel_project.payment_jpa.member_coupon.dto;


import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.member_jpa.member.dto.MemberEntity;
import com.hotel_project.payment_jpa.coupon.dto.CouponEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member_coupon_tbl")
public class MemberCouponEntity implements IMemberCoupon{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;

    @Transient
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "coupon_id",nullable = false)
    private CouponEntity couponEntity;

    @Transient
    private Long couponId;

    @Column(nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isUsed = false;

    @Column(nullable = true,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime usedAt;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = true,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Override
    public IId getMember(){
        return this.memberEntity;
    }

    @Override
    public void setMember(IId iId){
        if(iId == null){
            return;
        }
        if (this.memberEntity == null){
            this.memberEntity = new MemberEntity();
        }
        this.memberEntity.copyMembersId(iId);
    }

    @Override
    public Long getMemberId() {
        return this.memberEntity != null ? this.memberEntity.getId() : null;
    }

    @Override
    public void setMemberId(Long memberId) {
        if (memberId == null) {
            throw new IllegalStateException("set memberId: member is null");
        }
        if (this.memberEntity == null){
            this.memberEntity = new MemberEntity();
        }
        this.memberEntity.setId(memberId);
        this.memberId = memberId;
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
        if (this.couponEntity == null){
            this.couponEntity = new CouponEntity();
        }
        this.couponEntity.copyMembersId(iId);
    }

    @Override
    public Long getCouponId() {
        return this.couponEntity != null ? this.couponEntity.getId() : null;
    }

    @Override
    public void setCouponId(Long couponId) {
        if (this.couponId == null) {
            throw new IllegalStateException("set couponId: coupon is null");
        }
        if (this.couponEntity == null){
            this.couponEntity = new CouponEntity();
        }
        this.couponEntity.setId(couponId);
        this.couponId = couponId;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
