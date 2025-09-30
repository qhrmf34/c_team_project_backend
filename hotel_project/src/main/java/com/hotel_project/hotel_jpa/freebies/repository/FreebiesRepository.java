package com.hotel_project.hotel_jpa.freebies.repository;

import com.hotel_project.hotel_jpa.freebies.dto.FreebiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreebiesRepository extends JpaRepository<FreebiesEntity, Long> {

    boolean existsByFreebiesName(String countryName);

    boolean existsByFreebiesNameAndIdNot(String countryName, Long id);

    boolean existsById(Long id);
}