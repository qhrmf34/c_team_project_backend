package com.hotel_project.hotel_jpa.city.repository;

import com.hotel_project.hotel_jpa.city.dto.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {

    // 도시명 중복 체크
    boolean existsByCityName(String cityName);

    // 도시명 중복 체크 (특정 ID 제외)
    boolean existsByCityNameAndIdNot(String cityName, Long id);

    // ID 존재 여부 체크
    boolean existsById(Long id);
}
