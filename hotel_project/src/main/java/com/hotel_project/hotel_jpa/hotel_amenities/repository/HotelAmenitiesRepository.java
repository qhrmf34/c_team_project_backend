package com.hotel_project.hotel_jpa.hotel_amenities.repository;

import com.hotel_project.hotel_jpa.hotel_amenities.dto.HotelAmenitiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelAmenitiesRepository extends JpaRepository<HotelAmenitiesEntity, Long> {

    // 특정 호텔의 특정 편의시설 존재 여부 (중복 체크용)
    boolean existsByHotelEntity_IdAndAmenitiesEntity_Id(Long hotelId, Long amenitiesId);

    // 수정 시 중복 체크 (ID 제외하고 호텔+편의시설 조합 확인)
    boolean existsByHotelEntity_IdAndAmenitiesEntity_IdAndIdNot(Long hotelId, Long amenitiesId, Long id);

    // 특정 호텔의 편의시설 목록 조회
    List<HotelAmenitiesEntity> findByHotelEntity_Id(Long hotelId);

    // 특정 편의시설을 사용하는 호텔 목록 조회
    List<HotelAmenitiesEntity> findByAmenitiesEntity_Id(Long amenitiesId);

    // ID 존재 여부 체크 (JpaRepository에 기본 제공되지만 명시적으로 선언)
    boolean existsById(Long id);
}