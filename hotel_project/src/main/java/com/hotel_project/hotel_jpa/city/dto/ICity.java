package com.hotel_project.hotel_jpa.city.dto;

import com.hotel_project.hotel_jpa.freebies.dto.IFreebies;

public interface ICity {

    Long getId();
    void setId(Long id);
    String getCityName();
    void setCityName(String cityName);
    String getCityContent();
    void setCityContent(String cityContent);
    String getIdd();
    void setIdd(String idd);





    /*insert 용*/
    default void copyMembers(ICity iCity){
        setId(iCity.getId());
        setCityName(iCity.getCityName());
        setCityContent(iCity.getCityContent());
        setIdd(iCity.getIdd());
    }
    /*update 용*/
    default void copyNotNullMembers(ICity iCity){
        if(iCity.getId()!=null){
            setId(iCity.getId());
        }
        if(iCity.getCityName()!=null){
            setCityName(iCity.getCityName());
        }
        if(iCity.getCityContent()!=null){
            setCityContent(iCity.getCityContent());
        }
        if(iCity.getIdd()!=null){
            setIdd(iCity.getIdd());
        }
    }
}
