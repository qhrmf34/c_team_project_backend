package com.hotel_project.member_jpa.mail_authentication.dto;

import java.time.LocalDateTime;

public interface IMailAuthentication {
    Long getId();
    void setId(Long id);

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

    /* insert용: 모든 값 복사 */
    default void copyMailAuthentication(IMailAuthentication iMailAuth) {
        setId(iMailAuth.getId());
        setMemberId(iMailAuth.getMemberId());
        setCode(iMailAuth.getCode());
        setIsVerified(iMailAuth.getIsVerified());
        setCreatedAt(iMailAuth.getCreatedAt());
        setExpiresAt(iMailAuth.getExpiresAt());
    }

    /* update용: null 아닌 값만 복사 */
    default void copyNotNullMailAuthentication(IMailAuthentication iMailAuth) {
        if (iMailAuth.getId() != null) setId(iMailAuth.getId());
        if (iMailAuth.getMemberId() != null) setMemberId(iMailAuth.getMemberId());
        if (iMailAuth.getCode() != null) setCode(iMailAuth.getCode());
        if (iMailAuth.getIsVerified() != null) setIsVerified(iMailAuth.getIsVerified());
        if (iMailAuth.getCreatedAt() != null) setCreatedAt(iMailAuth.getCreatedAt());
        if (iMailAuth.getExpiresAt() != null) setExpiresAt(iMailAuth.getExpiresAt());
    }
}
