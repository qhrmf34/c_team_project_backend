package com.hotel_project.hotel_jpa.country.repository;

import com.hotel_project.hotel_jpa.country.dto.CountryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, Long> {

    // 국가명 중복 체크
    boolean existsByCountryName(String countryName);

    // 국가명 중복 체크 (특정 ID 제외)
    boolean existsByCountryNameAndIdNot(String countryName, Long id);

    // ID 존재 여부 체크
    boolean existsById(Long id);
}