package com.hotel_project.hotel_jpa.freebies.sevice;

import com.hotel_project.hotel_jpa.freebies.dto.FreebiesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FreebiesRepository extends JpaRepository<FreebiesEntity, Long> {
}
