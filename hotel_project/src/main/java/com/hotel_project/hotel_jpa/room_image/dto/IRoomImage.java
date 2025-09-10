package com.hotel_project.hotel_jpa.room_image.dto;

import java.time.LocalDateTime;

public interface IRoomImage {
    Long getId();
    void setId(Long id);

    Long getRoomId();
    void setRoomId(Long RoomId);

    String getRoomImageName();
    void setRoomImageName(String RoomImageName);

    String getRoomImagePath();
    void setRoomImagePath(String RoomImagePath);

    Long getRoomImageSize();
    void setRoomImageSize(Long RoomImageSize);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    /* insert용: 모든 값 복사 */
    default void copyMembers(IRoomImage iRoomImage) {
        setId(iRoomImage.getId());
        setRoomId(iRoomImage.getRoomId());
        setRoomImageName(iRoomImage.getRoomImageName());
        setRoomImagePath(iRoomImage.getRoomImagePath());
        setRoomImageSize(iRoomImage.getRoomImageSize());
        setCreatedAt(iRoomImage.getCreatedAt());
    }

    default void copyNotNullMembers(IRoomImage iRoomImage) {
        if  (iRoomImage.getId() != null) {
            setId(iRoomImage.getId());
        }
        if (iRoomImage.getRoomId() != null) {
            setRoomId(iRoomImage.getRoomId());
        }
        if (iRoomImage.getRoomImageName() != null) {
            setRoomImageName(iRoomImage.getRoomImageName());
        }
        if (iRoomImage.getRoomImagePath() != null) {
            setRoomImagePath(iRoomImage.getRoomImagePath());
        }
        if (iRoomImage.getRoomImageSize() != null) {
            setRoomImageSize(iRoomImage.getRoomImageSize());
        }
    }
}
