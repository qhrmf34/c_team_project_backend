package com.hotel_project.member_jpa.member.repository;

import com.hotel_project.member_jpa.member.dto.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    // 필요하면 추가 쿼리 메서드 작성 가능
}

