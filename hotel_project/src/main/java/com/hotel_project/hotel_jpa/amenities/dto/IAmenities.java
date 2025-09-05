package com.hotel_project.hotel_jpa.amenities.dto;

public interface IAmenities {
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
