package com.hotel_project.chat_jpa.chat.service;


import com.hotel_project.chat_jpa.chat.dto.ChatMessage;
import com.hotel_project.chat_jpa.chat.dto.ChatMessageEntity;
import com.hotel_project.chat_jpa.chat.dto.ChatRoom;
import com.hotel_project.chat_jpa.chat.repository.ChatMessageRepository;
import com.hotel_project.chat_jpa.chat.repository.ChatRoomRepository;
import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * 채팅방 생성 또는 기존 방 가져오기
     */
    public ChatRoom getOrCreateChatRoom(Long customerId, String customerName) {
        // 이미 활성화된 채팅방이 있는지 확인
        return chatRoomRepository.findByCustomerIdAndStatus(customerId, "ACTIVE")
                .orElseGet(() -> {
                    // 없으면 새로 생성
                    String roomId = "room_" + customerId + "_" + System.currentTimeMillis();
                    ChatRoom chatRoom = ChatRoom.builder()
                            .roomId(roomId)
                            .customerId(customerId)
                            .customerName(customerName)
                            .status("ACTIVE")
                            .unreadCount(0)
                            .build();
                    return chatRoomRepository.save(chatRoom);
                });
    }

    /**
     * 메시지 저장
     */
    public ChatMessageEntity saveMessage(ChatMessage message) {
        ChatMessageEntity entity = ChatMessageEntity.builder()
                .roomId(message.getRoomId())
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .senderType(message.getSenderType())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .isRead(message.isRead())
                .messageType(message.getType().toString())
                .build();

        // 채팅방 업데이트 시간 갱신
        chatRoomRepository.findByRoomId(message.getRoomId())
                .ifPresent(room -> {
                    room.setUpdatedAt(LocalDateTime.now());
                    // 고객 메시지면 관리자의 안 읽은 수 증가
                    if ("CUSTOMER".equals(message.getSenderType())) {
                        room.setUnreadCount(room.getUnreadCount() + 1);
                    }
                    chatRoomRepository.save(room);
                });

        return chatMessageRepository.save(entity);
    }

    /**
     * 채팅방의 메시지 목록 조회
     */
    public List<ChatMessage> getChatMessages(String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId)
                .stream()
                .map(entity -> ChatMessage.builder()
                        .type(ChatMessage.MessageType.valueOf(entity.getMessageType()))
                        .roomId(entity.getRoomId())
                        .senderId(entity.getSenderId())
                        .senderName(entity.getSenderName())
                        .senderType(entity.getSenderType())
                        .content(entity.getContent())
                        .timestamp(entity.getTimestamp())
                        .isRead(entity.isRead())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 모든 활성화된 채팅방 조회 (관리자용)
     */
    public List<ChatRoom> getActiveChatRooms() {
        return chatRoomRepository.findByStatusOrderByUpdatedAtDesc("ACTIVE");
    }

    /**
     * 메시지 읽음 처리
     */
    public void markMessagesAsRead(String roomId, String senderType) {
        List<ChatMessageEntity> unreadMessages = chatMessageRepository
                .findByRoomIdOrderByTimestampAsc(roomId)
                .stream()
                .filter(msg -> !msg.isRead() && msg.getSenderType().equals(senderType))
                .collect(Collectors.toList());

        unreadMessages.forEach(msg -> msg.setRead(true));
        chatMessageRepository.saveAll(unreadMessages);

        // 채팅방 안 읽은 수 초기화
        chatRoomRepository.findByRoomId(roomId)
                .ifPresent(room -> {
                    room.setUnreadCount(0);
                    chatRoomRepository.save(room);
                });
    }

    /**
     * 채팅방 닫기
     */
    public void closeChatRoom(String roomId) {
        chatRoomRepository.findByRoomId(roomId)
                .ifPresent(room -> {
                    room.setStatus("CLOSED");
                    chatRoomRepository.save(room);
                });
    }
}
