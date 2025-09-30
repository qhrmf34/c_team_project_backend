package com.hotel_project.hotel_jpa.hotel.repository;

import com.hotel_project.hotel_jpa.hotel.dto.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<HotelEntity, Long> {

    boolean existsByHotelName(String hotelName);

    boolean existsByHotelNameAndIdNot(String hotelName, Long id);

    boolean existsById(Long id);
}
