package com.hotel_project.review_jpa.choice.dto;

public interface IChoice {
    Long getId();
    void setId(Long id);
    String getName();
    void setName(String name);

    /*insert 용*/
    default void copyMembers(IChoice choice){
        setId(choice.getId());
        setName(choice.getName());
    }
    /*update 용*/
    default void copyNotNullMembers(IChoice choice){
        if(choice.getId()!=null){
            setId(choice.getId());
        }
        if(choice.getName()!=null){
            setName(choice.getName());
        }
    }
}
