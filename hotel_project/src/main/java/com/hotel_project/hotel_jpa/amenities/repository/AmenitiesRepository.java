package com.hotel_project.hotel_jpa.amenities.repository;

import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenitiesRepository extends JpaRepository<AmenitiesEntity, Long> {

    // 국가명 중복 체크
    boolean existsByAmenitiesName(String countryName);

    // 국가명 중복 체크 (특정 ID 제외)
    boolean existsByAmenitiesNameAndIdNot(String countryName, Long id);

    // ID 존재 여부 체크
    boolean existsById(Long id);
}