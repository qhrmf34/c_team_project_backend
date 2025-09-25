package com.hotel_project.payment_jpa.payment_method.dto;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.member_jpa.member.dto.MemberEntity;
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
@Table(name = "payment_method_tbl")
public class PaymentMethodEntity implements IPaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;

    @Transient
    private Long memberId;

    @Column(unique = true, nullable = false, length = 50)
    private String tossKey;

    @Column(length = 4)
    private String cardLastFour;

    @Column(length = 50)
    private String cardCompany;

    @Column(length = 20)
    private String cardType;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = true,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Override
    public IId getMember() {
        return this.memberEntity;
    }

    @Override
    public void setMember(IId iId) {
        if (iId == null) {
            return;
        }
        if (this.memberEntity == null) {
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
        if (this.memberEntity == null) {
            this.memberEntity = new MemberEntity();
        }
        this.memberEntity.setId(memberId);
        this.memberId = memberId;
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