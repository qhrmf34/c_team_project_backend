package com.hotel_project.hotel_jpa.freebies.repository;

import com.hotel_project.hotel_jpa.freebies.dto.FreebiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreebiesRepository extends JpaRepository<FreebiesEntity, Long> {

    // 무료시설명 중복 체크
    boolean existsByFreebiesName(String freebiesName);

    // 무료시설명 중복 체크 (특정 ID 제외)
    boolean existsByFreebiesNameAndIdNot(String freebiesName, Long id);

    // ID 존재 여부 체크
    boolean existsById(Long id);
}