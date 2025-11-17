package com.hotel_project.chat_jpa.chat.repository;

import com.hotel_project.chat_jpa.chat.dto.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomId(String roomId);
    List<ChatRoom> findByCustomerId(Long customerId);
    List<ChatRoom> findByStatusOrderByUpdatedAtDesc(String status);
    Optional<ChatRoom> findByCustomerIdAndStatus(Long customerId, String status);
}