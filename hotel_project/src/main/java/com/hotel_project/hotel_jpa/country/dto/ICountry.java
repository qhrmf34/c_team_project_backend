package com.hotel_project.hotel_jpa.country.dto;

import com.hotel_project.common_jpa.dto.IId;

public interface ICountry extends IId {

    Long getId();
    void setId(Long id);
    String getCountryName();
    void setCountryName(String countryName);
    String getIdd();
    void setIdd(String idd);





    /*insert 용*/
    default void copyMembers(ICountry iCountry) {
        setId(iCountry.getId());
        setCountryName(iCountry.getCountryName());
        setIdd(iCountry.getIdd());
    }
    /*update 용*/
    default void copyNotNullMembers(ICountry iCountry){
        if(iCountry.getId()!=null){
            setId(iCountry.getId());
        }
        if(iCountry.getCountryName()!=null){
            setCountryName(iCountry.getCountryName());
        }
        if(iCountry.getIdd()!=null){
            setIdd(iCountry.getIdd());
        }
    }
}
