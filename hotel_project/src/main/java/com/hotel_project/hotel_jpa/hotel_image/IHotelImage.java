package com.hotel_project.hotel_jpa.hotel_image;

import java.time.LocalDateTime;

public interface IHotelImage {
    Long getId();
    void setId(Long id);

    Long getHotelId();
    void setHotelId(Long hotelId);

    String getHotelImageName();
    void setHotelImageName(String hotelImageName);

    String getHotelImagePath();
    void setHotelImagePath(String hotelImagePath);

    Integer getHotelImageIndex();
    void setHotelImageIndex(Integer hotelImageIndex);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    /* insert용: 모든 값 복사 */
    default void copyMembers(IHotelImage iHotelImage) {
        setId(iHotelImage.getId());
        setHotelId(iHotelImage.getHotelId());
        setHotelImageName(iHotelImage.getHotelImageName());
        setHotelImagePath(iHotelImage.getHotelImagePath());
        setHotelImageIndex(iHotelImage.getHotelImageIndex());
        setCreatedAt(iHotelImage.getCreatedAt());
    }

    default void copyNotNullMembers(IHotelImage iHotelImage) {
        if  (iHotelImage.getId() != null) {
            setId(iHotelImage.getId());
        }
        if (iHotelImage.getHotelId() != null) {
            setHotelId(iHotelImage.getHotelId());
        }
        if (iHotelImage.getHotelImageName() != null) {
            setHotelImageName(iHotelImage.getHotelImageName());
        }
        if (iHotelImage.getHotelImagePath() != null) {
            setHotelImagePath(iHotelImage.getHotelImagePath());
        }
        if (iHotelImage.getHotelImageIndex() != null) {
            setHotelImageIndex(iHotelImage.getHotelImageIndex());
        }
    }
}
