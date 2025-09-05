package com.hotel_project.review_jpa.choice.dto;

public interface IChoice {
    Long getId();
    void setId(Long id);
    String getChoiceName();
    void setChoiceName(String choiceName);

    /*insert 용*/
    default void copyMembers(IChoice choice){
        setId(choice.getId());
        setChoiceName(choice.getChoiceName());
    }
    /*update 용*/
    default void copyNotNullMembers(IChoice choice){
        if(choice.getId()!=null){
            setId(choice.getId());
        }
        if(choice.getChoiceName()!=null){
            setChoiceName(choice.getChoiceName());
        }
    }
}
