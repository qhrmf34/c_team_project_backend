package com.hotel_project.hotel_jpa.city_image.repository;

import com.hotel_project.hotel_jpa.city_image.dto.CityImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityImageRepository extends JpaRepository<CityImageEntity, Long> {

    // ID 존재 여부 체크
    boolean existsById(Long id);

    // 도시별 이미지 개수 조회
    long countByCityEntityId(Long cityId);
}
