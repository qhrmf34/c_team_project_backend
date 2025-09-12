package com.hotel_project.hotel_jpa.hotel_freebies.dto;

import com.hotel_project.common_jpa.dto.IId;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IHotelFreebies extends IId {
    Long getId();
    void setId(Long id);

    Long getHotelId();
    void setHotelId(Long hotelId);

    Long getFreebiesId();
    void setFreebiesId(Long freebiesId);

    Boolean getIsAvailable();
    void setIsAvailable(Boolean isAvailable);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    default void copyMembers(IHotelFreebies iHotel_freebies) {
        setId(iHotel_freebies.getId());
        setHotelId(iHotel_freebies.getHotelId());
        setFreebiesId(iHotel_freebies.getFreebiesId());
        setIsAvailable(iHotel_freebies.getIsAvailable());
        setCreatedAt(iHotel_freebies.getCreatedAt());
        setUpdatedAt(iHotel_freebies.getUpdatedAt());
    }

    default void copyNotNullMembers(IHotelFreebies iHotel_freebies) {
        if (iHotel_freebies.getId() != null) { setId(iHotel_freebies.getId());}
        if (iHotel_freebies.getHotelId() != null) { setHotelId(iHotel_freebies.getHotelId());}
        if (iHotel_freebies.getFreebiesId() != null) { setFreebiesId(iHotel_freebies.getFreebiesId());}
        if (iHotel_freebies.getIsAvailable() != null) { setIsAvailable(iHotel_freebies.getIsAvailable());}
        if (iHotel_freebies.getUpdatedAt() != null) { setUpdatedAt(iHotel_freebies.getUpdatedAt());}
    }
}