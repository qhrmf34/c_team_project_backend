package com.hotel_project.review_jpa.reviews.dto;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.hotel.dto.HotelEntity;
import com.hotel_project.member_jpa.member.dto.MemberEntity;
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
@Table(name = "reviews_tbl")
public class ReviewsEntity implements IReviews {
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservations_id", nullable = false)
    private ReservationsEntity reservationsEntity;

    @Transient
    private Long reservationsId;

    @Column(columnDefinition = "DECIMAL(1,1) DEFAULT 0.0")
    private BigDecimal rating;

    @Lob
    private String reviewContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewCard reviewCard;

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
    public void setMember(IId iId) {
        if (iId == null){
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
            throw new IllegalArgumentException("memberId cannot be null");
        }
        if (this.memberEntity == null){
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
        if (iId == null){
            return;
        }
        if (this.hotelEntity == null){
            this.hotelEntity = new HotelEntity();
        }
        this.hotelEntity.copyMembersId(iId);
    }

    @Override
    public Long getHotelId() {
        return this.hotelEntity != null ? this.hotelEntity.getId() : null;
    }

    @Override
    public void setHotelId(Long hotelId) {
        if (hotelId == null) {
            throw new IllegalArgumentException("hotelId cannot be null");
        }
        if (this.hotelEntity == null){
            this.hotelEntity = new HotelEntity();
        }
        this.hotelEntity.setId(hotelId);
        this.hotelId = hotelId;
    }
    @Override
    public IId getReservations(){
        return this.reservationsEntity;
    }

    @Override
    public void setReservations(IId iId) {
        if (iId == null){
            return;
        }
        if (this.reservationsEntity == null){
            this.reservationsEntity = new ReservationsEntity();
        }
        this.reservationsEntity.copyMembersId(iId);
    }

    @Override
    public void setReservationsId(Long reservationsId) {
        if (reservationsId == null) {
            throw new IllegalArgumentException("reservationsId cannot be null");
        }
        if (this.reservationsEntity == null){
            this.reservationsEntity = new ReservationsEntity();
        }
        this.reservationsEntity.setId(reservationsId);
        this.reservationsId = reservationsId;
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
