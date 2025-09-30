package com.hotel_project.hotel_jpa.room_image.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomImageViewDto {
    private Long id;
    private String roomImageName;
    private String roomImagePath;
    private Long roomImageSize;
    private LocalDateTime createdAt;

    // 외래키 정보 (JOIN으로 가져온 데이터)
    private Long roomId;
    private String roomName;
}