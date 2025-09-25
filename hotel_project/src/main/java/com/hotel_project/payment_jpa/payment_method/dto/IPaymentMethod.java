package com.hotel_project.payment_jpa.payment_method.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;

import java.time.LocalDateTime;

public interface IPaymentMethod extends IId {
    Long getId();
    void setId(Long id);

    Long getMemberId();
    void setMemberId(Long memberId);

    @JsonIgnore
    IId getMember();
    void setMember(IId member);

    String getTossKey();
    void setTossKey(String tossKey);

    // 새로 추가되는 카드 정보 필드들
    String getCardLastFour();
    void setCardLastFour(String cardLastFour);

    String getCardCompany();
    void setCardCompany(String cardCompany);

    String getCardType();
    void setCardType(String cardType);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    //insert 용 - 새 필드들 추가
    default void copyMembers(IPaymentMethod iPaymentMethod) {
        if (iPaymentMethod == null){ return;}
        setId(iPaymentMethod.getId());
        setMember(iPaymentMethod.getMember());
        setTossKey(iPaymentMethod.getTossKey());
        setCardLastFour(iPaymentMethod.getCardLastFour());
        setCardCompany(iPaymentMethod.getCardCompany());
        setCardType(iPaymentMethod.getCardType());
        setCreatedAt(iPaymentMethod.getCreatedAt());
        setUpdatedAt(iPaymentMethod.getUpdatedAt());
    }

    //update 용 - 새 필드들 추가
    default void copyNotNullMembers(IPaymentMethod iPaymentMethod) {
        if (iPaymentMethod == null){ return;}
        if (iPaymentMethod.getId() != null) { setId(iPaymentMethod.getId()); }
        if (iPaymentMethod.getMember() != null) { setMember(iPaymentMethod.getMember()); }
        if (iPaymentMethod.getTossKey() != null) { setTossKey(iPaymentMethod.getTossKey()); }
        if (iPaymentMethod.getCardLastFour() != null) { setCardLastFour(iPaymentMethod.getCardLastFour()); }
        if (iPaymentMethod.getCardCompany() != null) { setCardCompany(iPaymentMethod.getCardCompany()); }
        if (iPaymentMethod.getCardType() != null) { setCardType(iPaymentMethod.getCardType()); }
        if (iPaymentMethod.getUpdatedAt() != null) { setUpdatedAt(iPaymentMethod.getUpdatedAt()); }
    }
}