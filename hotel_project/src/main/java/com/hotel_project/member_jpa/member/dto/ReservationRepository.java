package com.hotel_project.member_jpa.member.dto;

import com.hotel_project.member_jpa.member.dto.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByMemberId(Long memberId);
}
