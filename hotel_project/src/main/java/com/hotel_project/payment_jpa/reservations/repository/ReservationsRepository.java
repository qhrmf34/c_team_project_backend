package com.hotel_project.payment_jpa.reservations.repository;

import com.hotel_project.payment_jpa.reservations.dto.ReservationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ReservationsRepository extends JpaRepository<ReservationsEntity, Long> {

    /**
     * 중복 예약 체크
     */
    boolean existsByMemberEntity_IdAndRoomEntity_IdAndCheckInDateAndCheckOutDate(
            Long memberId,
            Long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    );



}