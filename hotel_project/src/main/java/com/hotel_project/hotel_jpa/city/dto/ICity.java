package com.hotel_project.hotel_jpa.city.dto;

import com.hotel_project.common_jpa.dto.IId;

public interface ICity extends IId {
    Long getId();
    void setId(Long id);

    Long getCountryId();
    void setCountryId(Long countryId);

    String getCityName();
    void setCityName(String cityName);

    String getCityContent();
    void setCityContent(String cityContent);

    /*insert 용*/
    default void copyMembers(ICity iCity) {
        setId(iCity.getId());
        setCountryId(iCity.getCountryId());
        setCityName(iCity.getCityName());
        setCityContent(iCity.getCityContent());
    }
    /*update 용*/
    default void copyNotNullMembers(ICity iCity){
        if(iCity.getId()!=null){
            setId(iCity.getId());
        }
        if(iCity.getCountryId()!=null){
            setCountryId(iCity.getCountryId());
        }
        if(iCity.getCityName()!=null){
            setCityName(iCity.getCityName());
        }
        if(iCity.getCityContent()!=null){
            setCityContent(iCity.getCityContent());
        }
    }
}
