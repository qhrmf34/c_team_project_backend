package com.hotel_project.hotel_jpa.hotel_image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.hotel.dto.IHotel;

import java.time.LocalDateTime;

public interface IHotelImage extends IId {
    Long getId();
    void setId(Long id);

    Long getHotelId();
    void setHotelId(Long hotelId);

    @JsonIgnore
    IId getHotel();
    void setHotel(IId hotel);

    String getHotelImageName();
    void setHotelImageName(String hotelImageName);

    String getHotelImagePath();
    void setHotelImagePath(String hotelImagePath);

    Long getHotelImageSize();
    void setHotelImageSize(Long hotelImageSize);

    Integer getHotelImageIndex();
    void setHotelImageIndex(Integer hotelImageIndex);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    /* insert용: 모든 값 복사 */
    default void copyMembers(IHotelImage iHotelImage) {
        if (iHotelImage == null) {
            return;
        }
        setId(iHotelImage.getId());
        setHotel(iHotelImage.getHotel());
        setHotelImageName(iHotelImage.getHotelImageName());
        setHotelImagePath(iHotelImage.getHotelImagePath());
        setHotelImageSize(iHotelImage.getHotelImageSize());
        setHotelImageIndex(iHotelImage.getHotelImageIndex());
        setCreatedAt(iHotelImage.getCreatedAt());
    }

    default void copyNotNullMembers(IHotelImage iHotelImage) {
        if (iHotelImage == null) {
            return;
        }
        if  (iHotelImage.getId() != null) {
            setId(iHotelImage.getId());
        }
        if (iHotelImage.getHotel() != null) {
            setHotel(iHotelImage.getHotel());
        }
        if (iHotelImage.getHotelImageName() != null) {
            setHotelImageName(iHotelImage.getHotelImageName());
        }
        if (iHotelImage.getHotelImagePath() != null) {
            setHotelImagePath(iHotelImage.getHotelImagePath());
        }
        if (iHotelImage.getHotelImageSize() != null) {
            setHotelImageSize(iHotelImage.getHotelImageSize());
        }
        if (iHotelImage.getHotelImageIndex() != null) {
            setHotelImageIndex(iHotelImage.getHotelImageIndex());
        }
    }
}
