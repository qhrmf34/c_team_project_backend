package com.hotel_project.hotel_jpa.room_pricing.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IRoomPricing extends IId {
    Long getId();
    void setId(Long id);

    Long getRoomId();
    void setRoomId(Long roomId);

    @JsonIgnore
    IId getRoom();
    void setRoom(IId room);

    LocalDate getDate();
    void setDate(LocalDate date);

    BigDecimal getPrice();
    void setPrice(BigDecimal price);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    default void copyMembers(IRoomPricing iRoomPricing) {
        setId(iRoomPricing.getId());
        setRoom(iRoomPricing.getRoom());
        setDate(iRoomPricing.getDate());
        setPrice(iRoomPricing.getPrice());
        setCreatedAt(iRoomPricing.getCreatedAt());
        setUpdatedAt(iRoomPricing.getUpdatedAt());
    }

    default void copyNotNullMembers(IRoomPricing iRoomPricing) {
        if (iRoomPricing.getId() != null) {setId(iRoomPricing.getId());}
        if (iRoomPricing.getRoom() != null) {setRoom(iRoomPricing.getRoom());}
        if (iRoomPricing.getDate() != null) {setDate(iRoomPricing.getDate());}
        if (iRoomPricing.getPrice() != null) {setPrice(iRoomPricing.getPrice());}
        if (iRoomPricing.getUpdatedAt() != null) {setUpdatedAt(iRoomPricing.getUpdatedAt());}
    }
}
