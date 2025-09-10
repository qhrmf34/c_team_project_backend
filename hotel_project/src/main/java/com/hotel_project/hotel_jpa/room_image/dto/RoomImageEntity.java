package com.hotel_project.hotel_jpa.room_image.dto;

import com.hotel_project.hotel_jpa.room.dto.RoomEntity;
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
@Table (name = "room_image_tbl")
public class RoomImageEntity implements IRoomImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    @Column(nullable = false, length = 255)
    private String roomImageName;

    @Column(nullable = false, length = 500)
    private String roomImagePath;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
    }

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
}
