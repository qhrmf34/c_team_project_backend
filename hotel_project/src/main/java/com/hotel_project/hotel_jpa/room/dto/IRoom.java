package com.hotel_project.hotel_jpa.room.dto;

import com.hotel_project.common_jpa.dto.IId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface IRoom extends IId {
    Long getId();
    void setId(Long id);

    Long getHotelId();
    void setHotelId(Long hotelId);

    String getRoomName();
    void setRoomName(String roomName);

    Byte getRoomSingleBed();
    void setRoomSingleBed(Byte roomSingleBed);

    Byte getRoomDoubleBed();
    void setRoomDoubleBed(Byte roomDoubleBed);

    BigDecimal getBasePrice();
    void setBasePrice(BigDecimal basePrice);

    Integer getRoomNumber();
    void setRoomNumber(Integer roomNumber);

    String getRoomView();
    void setRoomView(String roomView);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    default void copyMembers(IRoom iRoom) {
        setId(iRoom.getId());
        setHotelId(iRoom.getHotelId());
        setRoomName(iRoom.getRoomName());
        setRoomSingleBed(iRoom.getRoomSingleBed());
        setRoomDoubleBed(iRoom.getRoomDoubleBed());
        setBasePrice(iRoom.getBasePrice());
        setRoomNumber(iRoom.getRoomNumber());
        setRoomView(iRoom.getRoomView());
        setCreatedAt(iRoom.getCreatedAt());
        setUpdatedAt(iRoom.getUpdatedAt());
    }

    default void copyNotNullMembers(IRoom iRoom) {
        if (iRoom.getId() != null) { setId(iRoom.getId()); }
        if (iRoom.getHotelId() != null) { setHotelId(iRoom.getHotelId()); }
        if (iRoom.getRoomName() != null) { setRoomName(iRoom.getRoomName()); }
        if (iRoom.getRoomSingleBed() != null) { setRoomSingleBed(iRoom.getRoomSingleBed()); }
        if (iRoom.getRoomDoubleBed() != null) { setRoomDoubleBed(iRoom.getRoomDoubleBed()); }
        if (iRoom.getBasePrice() != null) { setBasePrice(iRoom.getBasePrice()); }
        if (iRoom.getRoomNumber() != null) { setRoomNumber(iRoom.getRoomNumber()); }
        if (iRoom.getRoomView() != null) { setRoomView(iRoom.getRoomView()); }
        if (iRoom.getUpdatedAt() != null) { setUpdatedAt(iRoom.getUpdatedAt()); }
    }
}
