package com.hotel_project.member_jpa.cart.dto;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.room.dto.RoomEntity;
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;

    @Transient
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity roomEntity;

    @Transient
    private Long roomId;

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
    public IId getRoom(){
        return this.roomEntity;
    }

    @Override
    public void setRoom(IId iId) {
        if (iId == null) {
            return;
        }
        if (this.roomEntity == null) {
            this.roomEntity = new RoomEntity();
        }
        this.memberEntity.copyMembersId(iId);
    }

    @Override
    public Long getRoomId(){
        return this.roomId != null ? this.roomEntity.getId() : null;
    }

    @Override
    public void setRoomId(Long roomId) {
        if (roomId == null) {
            throw new IllegalArgumentException("roomId cannot be null");
        }
        if (this.roomEntity == null) {
            this.roomEntity = new RoomEntity();
        }
        this.roomEntity.setId(roomId);
        this.roomId = roomId;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
    }
}
