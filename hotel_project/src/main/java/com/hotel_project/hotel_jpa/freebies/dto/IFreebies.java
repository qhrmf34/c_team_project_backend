package com.hotel_project.hotel_jpa.freebies.dto;


import com.hotel_project.common_jpa.dto.IId;

public interface IFreebies extends IId {

    Long getId();
    void setId(Long id);
    String getFreebiesName();
    void setFreebiesName(String freebiesName);

    /*insert 용*/
    default void copyMembers(IFreebies iFreebies){
        if(iFreebies == null){
            return;
        }
        setId(iFreebies.getId());
        setFreebiesName(iFreebies.getFreebiesName());
    }
    /*update 용*/
    default void copyNotNullMembers(IFreebies iFreebies){
        if(iFreebies == null){
            return;
        }
        if(iFreebies.getId()!=null){
            setId(iFreebies.getId());
        }
        if(iFreebies.getFreebiesName()!=null){
            setFreebiesName(iFreebies.getFreebiesName());
        }
    }
}
