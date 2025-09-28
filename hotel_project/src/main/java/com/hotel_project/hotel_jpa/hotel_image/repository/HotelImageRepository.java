package com.hotel_project.hotel_jpa.hotel_image.repository;

import com.hotel_project.hotel_jpa.hotel_image.dto.HotelImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelImageRepository extends JpaRepository<HotelImageEntity, Long> {

    // ID 존재 여부 체크
    boolean existsById(Long id);

    // 호텔별 이미지 개수 조회
    long countByHotelEntityId(Long HotelId);
}
