package com.hotel_project.hotel_jpa.room_pricing.dto;

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

    @ManyToOne
    @JoinColumn(name = "room_id")
    private RoomEntity room;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false,columnDefinition = "DECIMAL(10,2)")
    private BigDecimal price;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @Override
    public Long getRoomId() {
        if (this.room == null) {
            return 0L;
        }
        return this.room.getId();
    }

    @Override
    public void setRoomId(Long roomId) {
        if (this.room == null) {
            return;
        }
        this.room.setId(roomId);
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
