package com.hotel_project.hotel_jpa.amenities.dto;

import com.hotel_project.common_jpa.dto.IId;

public interface IAmenities extends IId {
    Long getId();
    void setId(Long id);
    String getAmenitiesName();
    void setAmenitiesName(String amenitiesName);

    /*insert 용*/
    default void copyMembers(IAmenities iamenities){
        setId(iamenities.getId());
        setAmenitiesName(iamenities.getAmenitiesName());
    }
    /*update 용*/
    default void copyNotNullMembers(IAmenities iamenities){
        if(iamenities.getId()!=null){
            setId(iamenities.getId());
        }
        if(iamenities.getAmenitiesName()!=null){
            setAmenitiesName(iamenities.getAmenitiesName());
        }
    }
}
