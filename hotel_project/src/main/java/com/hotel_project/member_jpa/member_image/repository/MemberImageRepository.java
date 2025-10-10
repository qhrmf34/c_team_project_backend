package com.hotel_project.member_jpa.member_image.repository;

import com.hotel_project.member_jpa.member_image.dto.ImageType;
import com.hotel_project.member_jpa.member_image.dto.MemberImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberImageRepository extends JpaRepository<MemberImageEntity, Long> {
    Optional<MemberImageEntity> findByMemberEntity_IdAndImageType(Long memberId, ImageType imageType);
}