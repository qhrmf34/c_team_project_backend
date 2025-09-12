package com.hotel_project.hotel_jpa.hotel_amenities.dto;

import com.hotel_project.common_jpa.dto.IId;

import java.time.LocalDateTime;

public interface IHotelAmenities extends IId {
    Long getId();
    void setId(Long id);

    Long getHotelId();
    void setHotelId(Long hotelId);

    Long getAmenitiesId();
    void setAmenitiesId(Long amenitiesId);

    Boolean getIsAvailable();
    void setIsAvailable(Boolean isAvailable);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    default void copyMembers(IHotelAmenities iHotelAmenities) {
        setId(iHotelAmenities.getId());
        setHotelId(iHotelAmenities.getHotelId());
        setAmenitiesId(iHotelAmenities.getAmenitiesId());
        setIsAvailable(iHotelAmenities.getIsAvailable());
        setCreatedAt(iHotelAmenities.getCreatedAt());
        setUpdatedAt(iHotelAmenities.getUpdatedAt());
    }
    default void copyNotNullMembers(IHotelAmenities iHotelAmenities) {
        if (iHotelAmenities.getId() != null){ setId(iHotelAmenities.getId()); }
        if (iHotelAmenities.getHotelId() != null){ setHotelId(iHotelAmenities.getHotelId()); }
        if (iHotelAmenities.getAmenitiesId() != null){ setAmenitiesId(iHotelAmenities.getAmenitiesId()); }
        if (iHotelAmenities.getIsAvailable() != null){ setIsAvailable(iHotelAmenities.getIsAvailable()); }
        if (iHotelAmenities.getUpdatedAt() != null){ setUpdatedAt(iHotelAmenities.getUpdatedAt()); }
    }
}
