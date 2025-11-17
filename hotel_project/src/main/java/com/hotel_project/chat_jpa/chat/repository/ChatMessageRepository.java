package com.hotel_project.chat_jpa.chat.repository;

import com.hotel_project.chat_jpa.chat.dto.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findByRoomIdOrderByTimestampAsc(String roomId);
    long countByRoomIdAndIsReadFalseAndSenderType(String roomId, String senderType);
}