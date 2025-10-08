package com.hotel_project.hotel_jpa.hotel.dto;

import com.hotel_project.common_jpa.dto.IId;
import java.math.BigDecimal;

public interface IHotelSummary extends IId {
    Long getId();
    void setId(Long id);

    String getTitle();
    void setTitle(String title);

    String getImage();
    void setImage(String image);

    Integer getImageCount();
    void setImageCount(Integer imageCount);

    BigDecimal getPrice();
    void setPrice(BigDecimal price);

    String getCurrency();
    void setCurrency(String currency);

    String getLocation();
    void setLocation(String location);

    Integer getStars();
    void setStars(Integer stars);

    String getType();
    void setType(String type);

    HotelType getHotelType();
    void setHotelType(HotelType hotelType);

    Integer getAmenitiesCount();
    void setAmenitiesCount(Integer amenitiesCount);

    BigDecimal getRating();
    void setRating(BigDecimal rating);

    String getRatingText();
    void setRatingText(String ratingText);

    Integer getReviewCount();
    void setReviewCount(Integer reviewCount);

    Boolean getWishlisted();
    void setWishlisted(Boolean wishlisted);

    String getCityName();
    void setCityName(String cityName);

    default void copyMembers(IHotelSummary source) {
        if (source == null) return;
        setId(source.getId());
        setTitle(source.getTitle());
        setImage(source.getImage());
        setImageCount(source.getImageCount());
        setPrice(source.getPrice());
        setCurrency(source.getCurrency());
        setLocation(source.getLocation());
        setStars(source.getStars());
        setType(source.getType());
        setHotelType(source.getHotelType());
        setAmenitiesCount(source.getAmenitiesCount());
        setRating(source.getRating());
        setRatingText(source.getRatingText());
        setReviewCount(source.getReviewCount());
        setWishlisted(source.getWishlisted());
        setCityName(source.getCityName());
    }

    default void copyNotNullMembers(IHotelSummary source) {
        if (source == null) return;
        if (source.getId() != null) setId(source.getId());
        if (source.getTitle() != null) setTitle(source.getTitle());
        if (source.getImage() != null) setImage(source.getImage());
        if (source.getImageCount() != null) setImageCount(source.getImageCount());
        if (source.getPrice() != null) setPrice(source.getPrice());
        if (source.getCurrency() != null) setCurrency(source.getCurrency());
        if (source.getLocation() != null) setLocation(source.getLocation());
        if (source.getStars() != null) setStars(source.getStars());
        if (source.getType() != null) setType(source.getType());
        if (source.getHotelType() != null) setHotelType(source.getHotelType());
        if (source.getAmenitiesCount() != null) setAmenitiesCount(source.getAmenitiesCount());
        if (source.getRating() != null) setRating(source.getRating());
        if (source.getRatingText() != null) setRatingText(source.getRatingText());
        if (source.getReviewCount() != null) setReviewCount(source.getReviewCount());
        if (source.getWishlisted() != null) setWishlisted(source.getWishlisted());
        if (source.getCityName() != null) setCityName(source.getCityName());
    }
}