package com.hotel_project.chat_jpa.chat.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    public enum MessageType {
        CHAT,      // 일반 채팅
        JOIN,      // 입장
        LEAVE,     // 퇴장
        FAQ        // 자주 묻는 질문
    }

    private MessageType type;
    private String roomId;          // 채팅방 ID (고객ID_timestamp)
    private Long senderId;          // 발신자 ID
    private String senderName;      // 발신자 이름
    private String senderType;      // CUSTOMER, ADMIN
    private String content;         // 메시지 내용
    private LocalDateTime timestamp;
    private boolean isRead;         // 읽음 여부
}