package com.hotel_project.member_jpa.member.dto;

import com.hotel_project.member_jpa.member.dto.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
}
