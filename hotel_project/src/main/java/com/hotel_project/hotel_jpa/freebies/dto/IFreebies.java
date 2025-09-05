package com.hotel_project.hotel_jpa.freebies.dto;


public interface IFreebies {

    Long getId();
    void setId(Long id);
    String getFreebiesName();
    void setFreebiesName(String freebiesName);

    /*insert 용*/
    default void copyMembers(IFreebies iFreebies){
        setId(iFreebies.getId());
        setFreebiesName(iFreebies.getFreebiesName());
    }
    /*update 용*/
    default void copyNotNullMembers(IFreebies iFreebies){
        if(iFreebies.getId()!=null){
            setId(iFreebies.getId());
        }
        if(iFreebies.getFreebiesName()!=null){
            setFreebiesName(iFreebies.getFreebiesName());
        }
    }
}
