package com.hotel_project.hotel_jpa.room.dto;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.hotel.dto.HotelEntity;
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
@Table (name = "room_tbl")
public class RoomEntity implements IRoom{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotelEntity;

    @Transient
    private Long hotelId;

    private String roomName;

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Byte roomSingleBed = 0;

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Byte roomDoubleBed = 0;

    @Column(columnDefinition = "DECIMAL(10,2)")
    private BigDecimal basePrice;

    @Column(nullable = false)
    private Integer roomNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ViewType roomView;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = true,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Override
    public IId getHotel(){
        return this.hotelEntity;
    }

    @Override
    public void setHotel(IId iId) {
        if(iId == null){
            return;
        }
        if(this.hotelEntity == null){
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
        if (hotelId == null){
            throw new IllegalArgumentException("hotelId cannot be null");
        }
        if(this.hotelEntity == null){
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
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
