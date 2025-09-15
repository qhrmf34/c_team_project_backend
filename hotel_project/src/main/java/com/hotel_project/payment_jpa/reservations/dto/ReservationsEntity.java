package com.hotel_project.payment_jpa.reservations.dto;


import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.room.dto.RoomEntity;
import com.hotel_project.member_jpa.member.dto.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservations_tbl")
public class ReservationsEntity implements IReservations{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;

    @Transient
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity roomEntity;

    @Transient
    private Long roomId;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer guestsCount = 1;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal basePayment;

    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Boolean reservationsStatus = false;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime reservationsAt;
    @Column(nullable = true,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Override
    public IId getMember(){
        return memberEntity;
    }

    @Override
    public void setMember(IId iId){
        if (iId == null){
            return;
        }
        if(this.memberEntity == null){
            this.memberEntity = new MemberEntity();
        }
        this.memberEntity.copyMembersId(iId);
    }

    @Override
    public Long getMemberId(){
        return this.memberEntity != null ? this.memberEntity.getId() : null;
    }
    @Override
    public void setMemberId(Long memberId){
        if (memberId == null){
            throw new IllegalArgumentException("memberId cannot be null");
        }
        if (this.memberEntity == null){
            this.memberEntity = new MemberEntity();
        }
        this.memberEntity.setId(memberId);
        this.memberId = memberId;
    }

    @Override
    public IId getRoom(){
        return roomEntity;
    }

    @Override
    public void setRoom(IId iId){
        if (iId == null){
            return;
        }
        if (this.roomEntity == null){
            this.roomEntity = new RoomEntity();
        }
        this.roomEntity.copyMembersId(iId);
    }

    @Override
    public Long getRoomId(){
        return this.roomEntity != null ? this.roomEntity.getId() : null;
    }

    @Override
    public void setRoomId(Long roomId){
        if (roomId == null){
            throw new IllegalArgumentException("roomId cannot be null");
        }
        if (this.roomEntity == null){
            this.roomEntity = new RoomEntity();
        }
        this.roomEntity.setId(roomId);
        this.roomId = roomId;
    }
}
