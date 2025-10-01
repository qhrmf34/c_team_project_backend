package com.hotel_project.hotel_jpa.room.repository;

import com.hotel_project.hotel_jpa.room.dto.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, Long> {

    boolean existsByRoomNameAndIdNot(String roomName, Long id);

    boolean existsById(Long id);
}
