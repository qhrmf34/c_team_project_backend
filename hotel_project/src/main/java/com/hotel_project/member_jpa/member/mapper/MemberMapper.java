package com.hotel_project.member_jpa.member.mapper;

import com.hotel_project.member_jpa.member.dto.MemberDto;
import com.hotel_project.member_jpa.member.dto.Provider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberMapper {

    // ID로 회원 조회
    MemberDto findById(@Param("id") Long id);

    // 이메일과 Provider로 회원 조회 (일반 로그인용)
    MemberDto findByEmailAndProvider(@Param("email") String email, @Param("provider") Provider provider);

    // Provider와 ProviderId로 회원 조회 (소셜 로그인용)
    MemberDto findByProviderAndProviderId(@Param("provider") Provider provider, @Param("providerId") String providerId);
    
}