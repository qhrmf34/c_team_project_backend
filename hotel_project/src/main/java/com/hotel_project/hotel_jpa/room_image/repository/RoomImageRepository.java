package com.hotel_project.hotel_jpa.room_image.repository;

import com.hotel_project.hotel_jpa.room_image.dto.RoomImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImageEntity, Long> {

    // ID 존재 여부 체크
    boolean existsById(Long id);

    // 객실별 이미지 개수 조회 아직은 사용 안하나 혹시 모르니 냅둠
    // long countByRoomEntityId(Long RoomId);
}
