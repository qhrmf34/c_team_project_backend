package com.hotel_project.hotel_jpa.hotel_freebies.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IHotelFreebies extends IId {
    Long getId();
    void setId(Long id);

    Long getHotelId();
    void setHotelId(Long hotelId);

    @JsonIgnore
    IId getHotel();
    void setHotel(IId hotel);

    Long getFreebiesId();
    void setFreebiesId(Long freebiesId);

    @JsonIgnore
    IId getFreebies();
    void setFreebies(IId freebies);


    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    default void copyMembers(IHotelFreebies iHotel_freebies) {
        if (iHotel_freebies == null) {
            return;
        }
        setId(iHotel_freebies.getId());
        setHotel(iHotel_freebies.getHotel());
        setFreebies(iHotel_freebies.getFreebies());
        setCreatedAt(iHotel_freebies.getCreatedAt());
        setUpdatedAt(iHotel_freebies.getUpdatedAt());
    }

    default void copyNotNullMembers(IHotelFreebies iHotel_freebies) {
        if (iHotel_freebies == null) {
            return;
        }
        if (iHotel_freebies.getId() != null) { setId(iHotel_freebies.getId());}
        if (iHotel_freebies.getHotel() != null) { setHotel(iHotel_freebies.getHotel());}
        if (iHotel_freebies.getFreebies() != null) { setFreebies(iHotel_freebies.getFreebies());}
        if (iHotel_freebies.getUpdatedAt() != null) { setUpdatedAt(iHotel_freebies.getUpdatedAt());}
    }
}