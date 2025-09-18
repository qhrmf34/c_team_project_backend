package com.hotel_project.member_jpa.cart.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;

import java.time.LocalDateTime;

public interface ICart extends IId {
    Long getId();
    void setId(Long id);

    Long getMemberId();
    void setMemberId(Long memberId);

    @JsonIgnore
    IId getMember();
    void setMember(IId member);

    Long getRoomId();
    void setRoomId(Long roomId);

    @JsonIgnore
    IId getRoom();
    void setRoom(IId room);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    default void copyMembers(ICart iCart) {
        if (iCart == null) {
            return;
        }
        setId(iCart.getId());
        setMember(iCart.getMember());
        setRoom(iCart.getRoom());
        setCreatedAt(iCart.getCreatedAt());
    }

    default void copyNotNullMembers(ICart iCart) {
        if (iCart == null) {
            return;
        }
        if (iCart.getId() != null) {setId(iCart.getId());}
        if (iCart.getMember() != null) {setMember(iCart.getMember());}
        if (iCart.getRoom() != null) {setRoom(iCart.getRoom());}
    }
}
