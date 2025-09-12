package com.hotel_project.payment_jpa.payment_method.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;

import java.time.LocalDateTime;

public interface IPaymentMethod extends IId {
    Long getId();
    void setId(Long id);

    Long getMemberId();
    void setMemberId(Long memberId); // 이게 굳이 있을이유가 있나?

    @JsonIgnore
    IId getMember();
    void setMember(IId member);

    String getTossKey();
    void setTossKey(String tossKey);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    //insert 용
    default void copyMembers(IPaymentMethod iPaymentMethod) {
        if (iPaymentMethod == null){ return;}
        setId(iPaymentMethod.getId());
        setMember(iPaymentMethod.getMember());
        setTossKey(iPaymentMethod.getTossKey());
        setCreatedAt(iPaymentMethod.getCreatedAt());
        setUpdatedAt(iPaymentMethod.getUpdatedAt());
    }

    //update 용
    default void copyNotNullMembers(IPaymentMethod iPaymentMethod) {
        if (iPaymentMethod == null){ return;}
        if (iPaymentMethod.getId() != null) { setId(iPaymentMethod.getId()); }
        if (iPaymentMethod.getMember() != null) { setMember(iPaymentMethod.getMember()); }
        if (iPaymentMethod.getTossKey() != null) { setTossKey(iPaymentMethod.getTossKey()); }
        if (iPaymentMethod.getUpdatedAt() != null) { setUpdatedAt(iPaymentMethod.getUpdatedAt()); }
    }
}

