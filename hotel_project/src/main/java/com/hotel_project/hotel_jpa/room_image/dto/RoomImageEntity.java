package com.hotel_project.hotel_jpa.room_image.dto;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.room.dto.RoomEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table (name = "room_image_tbl")
public class RoomImageEntity implements IRoomImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity roomEntity;

    @Transient
    private Long roomId;

    @Column(nullable = false, length = 255)
    private String roomImageName;

    @Column(nullable = false, length = 500)
    private String roomImagePath;

    private Long roomImageSize;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;
    @Override
    public IId getRoom(){
        return this.roomEntity;
    }

    @Override
    public void setRoom(IId iId){
        if (iId == null) {
            return;
        }
        if (this.roomEntity == null) {
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
            throw new IllegalArgumentException("Room Id cannot be null");
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
