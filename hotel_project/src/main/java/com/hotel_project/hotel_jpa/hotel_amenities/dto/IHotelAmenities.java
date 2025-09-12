package com.hotel_project.hotel_jpa.hotel_amenities.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;

import java.time.LocalDateTime;

public interface IHotelAmenities extends IId {
    Long getId();
    void setId(Long id);

    Long getHotelId();
    void setHotelId(Long hotelId);

    @JsonIgnore
    IId getHotel();
    void setHotel(IId hotel);

    Long getAmenitiesId();
    void setAmenitiesId(Long amenitiesId);

    @JsonIgnore
    IId getAmenities();
    void setAmenities(IId amenities);

    Boolean getIsAvailable();
    void setIsAvailable(Boolean isAvailable);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    default void copyMembers(IHotelAmenities iHotelAmenities) {
        if(iHotelAmenities == null){
            return;
        }
        setId(iHotelAmenities.getId());
        setHotel(iHotelAmenities.getHotel());
        setAmenities(iHotelAmenities.getAmenities());
        setIsAvailable(iHotelAmenities.getIsAvailable());
        setCreatedAt(iHotelAmenities.getCreatedAt());
        setUpdatedAt(iHotelAmenities.getUpdatedAt());
    }
    default void copyNotNullMembers(IHotelAmenities iHotelAmenities) {
        if(iHotelAmenities == null){
            return;
        }
        if (iHotelAmenities.getId() != null){ setId(iHotelAmenities.getId()); }
        if (iHotelAmenities.getHotel() != null){ setHotel(iHotelAmenities.getHotel()); }
        if (iHotelAmenities.getAmenities() != null){ setAmenities(iHotelAmenities.getAmenities()); }
        if (iHotelAmenities.getIsAvailable() != null){ setIsAvailable(iHotelAmenities.getIsAvailable()); }
        if (iHotelAmenities.getUpdatedAt() != null){ setUpdatedAt(iHotelAmenities.getUpdatedAt()); }
    }
}
