package com.hotel_project.member_jpa.mail_authentication.repository;

import com.hotel_project.member_jpa.mail_authentication.dto.MailAuthenticationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MailAuthenticationRepository extends JpaRepository<MailAuthenticationEntity, Long> {

    // 특정 회원의 미인증 코드들 삭제
    void deleteByMemberEntityIdAndIsVerifiedFalse(Long memberId);

    // 만료된 인증 코드 삭제
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}