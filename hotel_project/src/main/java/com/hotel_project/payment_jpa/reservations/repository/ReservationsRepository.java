package com.hotel_project.payment_jpa.reservations.repository;

import com.hotel_project.payment_jpa.reservations.dto.ReservationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ReservationsRepository extends JpaRepository<ReservationsEntity, Long> {

    boolean existsByMemberEntity_IdAndRoomEntity_IdAndCheckInDateAndCheckOutDate(
            Long memberId,
            Long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    );

    Optional<ReservationsEntity> findByMemberEntity_IdAndRoomEntity_IdAndCheckInDateAndCheckOutDateAndReservationsStatusFalse(
            Long memberId,
            Long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate
    );
}