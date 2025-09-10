package com.hotel_project.hotel_jpa.room.dto;

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

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    private HotelEntity hotel;

    private String roomName;

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Byte roomSingleBed = 0;

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Byte roomDoubleBed = 0;

    @Column(columnDefinition = "DECIMAL(10,2)")
    private BigDecimal basePrice;

    @Column(nullable = false)
    private Integer roomNumber;

    @Column(nullable = false, length = 30)
    private String roomView;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(nullable = true,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Override
    public Long getHotelId() {
        if (this.hotel == null) {
            return 0L;
        }
        return this.hotel.getId();
    }

    @Override
    public void setHotelId(Long hotelId) {
        if(this.hotel == null){
            return;
        }
        this.hotel.setId(hotelId);
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
