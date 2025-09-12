package com.hotel_project.common_jpa.dto;

public interface IId {
    Long getId();
    void setId(Long id);

    default void copyMembersId(IId iId) {
        if ( iId == null ) {
            return;
        }
        this.setId(iId.getId());
    }

    default void copyMembersIdNotNull(IId iId) {
        if ( iId == null ) {
            return;
        }
        if (iId.getId() != null) {
            this.setId(iId.getId());
        }
    }
}
