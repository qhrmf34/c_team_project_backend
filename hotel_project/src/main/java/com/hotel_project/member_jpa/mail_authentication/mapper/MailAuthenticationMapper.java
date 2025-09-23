// MailAuthenticationMapper.java
package com.hotel_project.member_jpa.mail_authentication.mapper;

import com.hotel_project.member_jpa.mail_authentication.dto.MailAuthenticationDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MailAuthenticationMapper {

    // 특정 회원의 최신 미인증 코드 조회
    MailAuthenticationDto findLatestUnverifiedByMemberId(@Param("memberId") Long memberId);

    // 회원 ID와 코드로 미인증 코드 조회
    MailAuthenticationDto findByMemberIdAndCode(@Param("memberId") Long memberId, @Param("code") String code);

    // 특정 인증 코드 조회 (ID로)
    MailAuthenticationDto findById(@Param("id") Long id);

    MailAuthenticationDto findVerifiedByMemberIdAndCode(@Param("memberId") Long memberId, @Param("code") String code);

}