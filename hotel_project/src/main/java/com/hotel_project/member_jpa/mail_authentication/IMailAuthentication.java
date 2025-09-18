package com.hotel_project.member_jpa.mail_authentication;

import com.hotel_project.common_jpa.dto.IId;

import java.time.LocalDateTime;

public interface IMailAuthentication extends IId {
    Long getId();
    void setId(Long id);

    IId getMember();
    void setMember(IId member);

    Long getMemberId();
    void setMemberId(Long memberId);

    String getCode();
    void setCode(String code);

    Boolean getIsVerified();
    void setIsVerified(Boolean isVerified);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getExpiresAt();
    void setExpiresAt(LocalDateTime expiresAt);

    default void copyMembers(IMailAuthentication iMailAuthentication) {
        if (iMailAuthentication == null) {
            return;
        }
        setId(iMailAuthentication.getId());
        setMember(iMailAuthentication.getMember());
        setCode(iMailAuthentication.getCode());
        setIsVerified(iMailAuthentication.getIsVerified());
        setCreatedAt(iMailAuthentication.getCreatedAt());
        setExpiresAt(iMailAuthentication.getExpiresAt());
    }

    default void copyNotNullMembers(IMailAuthentication iMailAuthentication) {
        if (iMailAuthentication == null) {
            return;
        }
        if (iMailAuthentication.getId() != null) { setId(iMailAuthentication.getId()); }
        if (iMailAuthentication.getMember() != null) { setMember(iMailAuthentication.getMember()); }
        if (iMailAuthentication.getCode() != null) { setCode(iMailAuthentication.getCode()); }
        if (iMailAuthentication.getIsVerified() != null) { setIsVerified(iMailAuthentication.getIsVerified()); }
    }
}
