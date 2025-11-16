package com.hotel_project.member_jpa.member.repository;

import com.hotel_project.member_jpa.member.dto.MemberEntity;
import com.hotel_project.member_jpa.member.dto.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    // 중복 체크용 메서드들
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByProviderAndProviderId(Provider provider, String providerId);

    // 업데이트시 중복 체크용 (본인 제외)
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByPhoneNumberAndIdNot(String phoneNumber, Long id);

    // ID 존재 여부 체크
    boolean existsById(Long id);

    // 탈퇴 회원 정리용
    List<MemberEntity> findByProviderAndDeletedAtBefore(Provider provider, LocalDateTime dateTime);

    // providerId로 모든 회원 조회 (탈퇴 회원 포함)
    List<MemberEntity> findByProviderId(String providerId);
}