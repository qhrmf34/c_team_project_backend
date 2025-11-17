package com.hotel_project.member_jpa.member_image.repository;

import com.hotel_project.member_jpa.member_image.dto.ImageType;
import com.hotel_project.member_jpa.member_image.dto.MemberImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberImageRepository extends JpaRepository<MemberImageEntity, Long> {
    Optional<MemberImageEntity> findByMemberEntity_IdAndImageType(Long memberId, ImageType imageType);

    // ✅ 여러 회원의 프로필 이미지 조회 (관리자용)
    List<MemberImageEntity> findByMemberEntity_IdInAndImageType(List<Long> memberIds, ImageType imageType);

}