package com.hotel_project.hotel_jpa.hotel_freebies.repository;

import com.hotel_project.hotel_jpa.hotel_freebies.dto.HotelFreebiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelFreebiesRepository extends JpaRepository<HotelFreebiesEntity, Long> {

    // 특정 호텔의 특정 무료시설 존재 여부 (중복 체크용)
    boolean existsByHotelEntity_IdAndFreebiesEntity_Id(Long hotelId, Long freebiesId);

    // 수정 시 중복 체크 (ID 제외하고 호텔+무료시설 조합 확인)
    boolean existsByHotelEntity_IdAndFreebiesEntity_IdAndIdNot(Long hotelId, Long freebiesId, Long id);

    // 특정 호텔의 무료시설 목록 조회
    List<HotelFreebiesEntity> findByHotelEntity_Id(Long hotelId);

    // 특정 무료시설을 사용하는 호텔 목록 조회
    List<HotelFreebiesEntity> findByFreebiesEntity_Id(Long freebiesId);

    // ID 존재 여부 체크 (JpaRepository에 기본 제공되지만 명시적으로 선언)
    boolean existsById(Long id);
}