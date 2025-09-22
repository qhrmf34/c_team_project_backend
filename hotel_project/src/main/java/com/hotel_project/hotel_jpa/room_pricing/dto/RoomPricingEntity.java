package com.hotel_project.hotel_jpa.room_pricing.dto;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.room.dto.RoomEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room_pricing_tbl")
public class RoomPricingEntity implements IRoomPricing{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity roomEntity;

    @Transient
    private Long roomId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false,columnDefinition = "DECIMAL(10,2)")
    private BigDecimal price;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = true,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Override
    public IId getRoom(){
        return this.roomEntity;
    }

    @Override
    public void setRoom(IId iId) {
        if(iId == null){
            return;
        }
        if(this.roomEntity == null){
            this.roomEntity = new RoomEntity();
        }
        this.roomEntity.copyMembersId(iId);
    }

    @Override
    public Long getRoomId() {
        return this.roomEntity != null ? this.roomEntity.getId() : null;
    }

    @Override
    public void setRoomId(Long roomId) {
        if (roomId == null) {
           throw new IllegalArgumentException("Room id cannot be null");
        }
        if(this.roomEntity == null){
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
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
