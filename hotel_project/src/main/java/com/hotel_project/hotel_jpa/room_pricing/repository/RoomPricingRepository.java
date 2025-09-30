package com.hotel_project.hotel_jpa.room_pricing.repository;

import com.hotel_project.hotel_jpa.room_pricing.dto.RoomPricingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RoomPricingRepository extends JpaRepository<RoomPricingEntity, Long> {

    // 특정 객실의 특정 날짜에 가격이 이미 존재하는지 확인
    boolean existsByRoomEntityIdAndDate(Long roomId, LocalDate date);

    // 자신을 제외하고 같은 객실의 같은 날짜가 있는지 확인
    boolean existsByRoomEntityIdAndDateAndIdNot(Long roomId, LocalDate date, Long id);

    boolean existsById(Long id);
}