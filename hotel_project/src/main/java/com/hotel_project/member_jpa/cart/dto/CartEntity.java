package com.hotel_project.member_jpa.cart.dto;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.hotel.dto.HotelEntity;
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
@Table(name = "cart_tbl")
public class CartEntity implements ICart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;

    @Transient
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotelEntity;

    @Transient
    private Long hotelId;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Override
    public IId getMember(){
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
    public Long getMemberId(){
        return this.memberEntity != null ? this.memberEntity.getId() : null;
    }

    @Override
    public void setMemberId(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("memberId cannot be null");
        }
        if (this.memberEntity == null) {
            this.memberEntity = new MemberEntity();
        }
        this.memberEntity.setId(memberId);
        this.memberId = memberId;
    }

    @Override
    public IId getHotel(){
        return this.hotelEntity;
    }

    @Override
    public void setHotel(IId iId) {
        if (iId == null) {
            return;
        }
        if (this.hotelEntity == null) {
            this.hotelEntity = new HotelEntity();
        }
        this.memberEntity.copyMembersId(iId);
    }

    @Override
    public Long getHotelId(){
        return this.hotelId != null ? this.hotelEntity.getId() : null;
    }

    @Override
    public void setHotelId(Long hotelId) {
        if (hotelId == null) {
            throw new IllegalArgumentException("roomId cannot be null");
        }
        if (this.hotelEntity == null) {
            this.hotelEntity = new HotelEntity();
        }
        this.hotelEntity.setId(hotelId);
        this.hotelId = hotelId;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
    }
}
