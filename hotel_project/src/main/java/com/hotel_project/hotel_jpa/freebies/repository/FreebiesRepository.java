package com.hotel_project.hotel_jpa.freebies.repository;

import com.hotel_project.hotel_jpa.freebies.dto.FreebiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreebiesRepository extends JpaRepository<FreebiesEntity, Long> {
    // JPA 기본 메서드만 사용 (save, deleteById 등)
}

