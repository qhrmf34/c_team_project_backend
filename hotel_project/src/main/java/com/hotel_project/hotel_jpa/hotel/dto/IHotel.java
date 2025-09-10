package com.hotel_project.hotel_jpa.hotel.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface IHotel {
    Long getId();
    void setId(Long id);

    Long getCityId();
    void setCityId(Long cityId);

    HotelType getHotelType();
    void setHotelType(HotelType hotelType);

    String getHotelName();
    void setHotelName(String hotelName);

    BigDecimal getHotelLatitude();
    void setHotelLatitude(BigDecimal hotelLatitude);

    BigDecimal getHotelLongitude();
    void setHotelLongitude(BigDecimal hotelLongitude);

    String getHotelContent();
    void setHotelContent(String hotelContent);

    Integer getHotelStar();
    void setHotelStar(Integer hotelStar);

    Integer getFreebiesNumber();
    void setFreebiesNumber(Integer freebiesNumber);

    String getHotelNumber();
    void setHotelNumber(String hotelNumber);

    LocalTime getCheckinTime();
    void setCheckinTime(LocalTime checkinTime);

    LocalTime getCheckoutTime();
    void setCheckoutTime(LocalTime checkoutTime);

    BigDecimal getHotelRating();
    void setHotelRating(BigDecimal hotelRating);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    default void copyMembers(IHotel iHotel) {
        setId(iHotel.getId());
        setCityId(iHotel.getCityId());
        setHotelType(iHotel.getHotelType());
        setHotelName(iHotel.getHotelName());
        setHotelLatitude(iHotel.getHotelLatitude());
        setHotelLongitude(iHotel.getHotelLongitude());
        setHotelContent(iHotel.getHotelContent());
        setHotelStar(iHotel.getHotelStar());
        setFreebiesNumber(iHotel.getFreebiesNumber());
        setHotelNumber(iHotel.getHotelNumber());
        setCheckinTime(iHotel.getCheckinTime());
        setCheckoutTime(iHotel.getCheckoutTime());
        setHotelRating(iHotel.getHotelRating());
        setCreatedAt(iHotel.getCreatedAt());
        setUpdatedAt(iHotel.getUpdatedAt());
    }

    default void copyNotNullMembers(IHotel iHotel) {
        if (iHotel.getId() != null) { setId(iHotel.getId()); }
        if (iHotel.getCityId() != null) { setCityId(iHotel.getCityId()); }
        if (iHotel.getHotelType() != null) { setHotelType(iHotel.getHotelType()); }
        if (iHotel.getHotelName() != null) { setHotelName(iHotel.getHotelName()); }
        if (iHotel.getHotelLatitude() != null) { setHotelLatitude(iHotel.getHotelLatitude()); }
        if (iHotel.getHotelLongitude() != null) { setHotelLongitude(iHotel.getHotelLongitude()); }
        if (iHotel.getHotelContent() != null) { setHotelContent(iHotel.getHotelContent()); }
        if (iHotel.getHotelStar() != null) { setHotelStar(iHotel.getHotelStar()); }
        if (iHotel.getFreebiesNumber() != null) { setFreebiesNumber(iHotel.getFreebiesNumber()); }
        if (iHotel.getHotelNumber() != null) { setHotelNumber(iHotel.getHotelNumber()); }
        if (iHotel.getCheckinTime() != null) { setCheckinTime(iHotel.getCheckinTime()); }
        if (iHotel.getCheckoutTime() != null) { setCheckoutTime(iHotel.getCheckoutTime()); }
        if (iHotel.getHotelRating() != null) { setHotelRating(iHotel.getHotelRating()); }
        if (iHotel.getUpdatedAt() != null) { setUpdatedAt(iHotel.getUpdatedAt()); }
    }
}
