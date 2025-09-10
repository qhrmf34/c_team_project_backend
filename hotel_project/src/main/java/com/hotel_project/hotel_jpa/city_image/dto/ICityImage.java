package com.hotel_project.hotel_jpa.city_image.dto;

import java.time.LocalDateTime;

public interface ICityImage {
    Long getId();
    void setId(Long id);

    Long getCityId();
    void setCityId(Long cityId);

    String getCityImageName();
    void setCityImageName(String cityImageName);

    String getCityImagePath();
    void setCityImagePath(String cityImagePath);

    Integer getCityImageIndex();
    void setCityImageIndex(Integer cityImageIndex);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    /* insert용: 모든 값 복사 */
    default void copyMembers(ICityImage iCityImage) {
        setId(iCityImage.getId());
        setCityId(iCityImage.getCityId());
        setCityImageName(iCityImage.getCityImageName());
        setCityImagePath(iCityImage.getCityImagePath());
        setCityImageIndex(iCityImage.getCityImageIndex());
        setCreatedAt(iCityImage.getCreatedAt());
    }

    default void copyNotNullMembers(ICityImage iCityImage) {
        if  (iCityImage.getId() != null) {
            setId(iCityImage.getId());
        }
        if (iCityImage.getCityId() != null) {
            setCityId(iCityImage.getCityId());
        }
        if (iCityImage.getCityImageName() != null) {
            setCityImageName(iCityImage.getCityImageName());
        }
        if (iCityImage.getCityImagePath() != null) {
            setCityImagePath(iCityImage.getCityImagePath());
        }
        if (iCityImage.getCityImageIndex() != null) {
            setCityImageIndex(iCityImage.getCityImageIndex());
        }
    }
}
