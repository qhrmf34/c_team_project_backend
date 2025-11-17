package com.hotel_project.chat_jpa.chat.controller;

import com.hotel_project.chat_jpa.chat.dto.ChatMessage;
import com.hotel_project.chat_jpa.chat.dto.ChatRoom;
import com.hotel_project.chat_jpa.chat.service.ChatService;
import com.hotel_project.common_jpa.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 메시지 전송
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        message.setRead(false);

        // DB에 저장
        chatService.saveMessage(message);

        // 해당 채팅방 구독자들에게 전송
        messagingTemplate.convertAndSend("/topic/chat/" + message.getRoomId(), message);

        // 관리자에게도 알림 (새 메시지)
        if ("CUSTOMER".equals(message.getSenderType())) {
            messagingTemplate.convertAndSend("/topic/admin/notifications", message);
        }
    }

    /**
     * 채팅방 입장
     */
    @MessageMapping("/chat.join")
    @SendTo("/topic/chat")
    public ChatMessage joinChat(@Payload ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        message.setType(ChatMessage.MessageType.JOIN);
        message.setTimestamp(LocalDateTime.now());
        headerAccessor.getSessionAttributes().put("username", message.getSenderName());
        headerAccessor.getSessionAttributes().put("roomId", message.getRoomId());

        chatService.saveMessage(message);
        return message;
    }

    /**
     * REST API - 채팅방 생성 또는 가져오기
     */
    @PostMapping("/api/chat/room")
    public ResponseEntity<ApiResponse<ChatRoom>> getOrCreateRoom(
            @RequestParam Long customerId,
            @RequestParam String customerName
    ) {
        ChatRoom room = chatService.getOrCreateChatRoom(customerId, customerName);
        return ResponseEntity.ok(ApiResponse.success(200, "채팅방 조회 완료", room));
    }

    /**
     * REST API - 채팅 메시지 조회
     */
    @GetMapping("/api/chat/messages/{roomId}")
    public ResponseEntity<ApiResponse<List<ChatMessage>>> getMessages(@PathVariable String roomId) {
        List<ChatMessage> messages = chatService.getChatMessages(roomId);
        return ResponseEntity.ok(ApiResponse.success(200, "메시지 조회 완료", messages));
    }

    /**
     * REST API - 메시지 읽음 처리
     */
    @PostMapping("/api/chat/read/{roomId}")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @PathVariable String roomId,
            @RequestParam String senderType
    ) {
        chatService.markMessagesAsRead(roomId, senderType);
        return ResponseEntity.ok(ApiResponse.success(200, "읽음 처리 완료", null));
    }

    /**
     * REST API - 활성 채팅방 목록 (관리자용)
     */
    @GetMapping("/api/chat/rooms/active")
    public ResponseEntity<ApiResponse<List<ChatRoom>>> getActiveRooms() {
        List<ChatRoom> rooms = chatService.getActiveChatRooms();
        return ResponseEntity.ok(ApiResponse.success(200, "채팅방 목록 조회 완료", rooms));
    }

    /**
     * REST API - 채팅방 닫기
     */
    @PostMapping("/api/chat/room/{roomId}/close")
    public ResponseEntity<ApiResponse<String>> closeRoom(@PathVariable String roomId) {
        chatService.closeChatRoom(roomId);
        return ResponseEntity.ok(ApiResponse.success(200, "채팅방이 종료되었습니다.", null));
    }
}