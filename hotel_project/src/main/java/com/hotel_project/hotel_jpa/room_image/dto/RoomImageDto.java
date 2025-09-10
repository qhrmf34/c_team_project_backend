package com.hotel_project.hotel_jpa.room_image.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomImageDto implements IRoomImage {
    private Long id;

    private Long roomId;

    @NotBlank(message = "객실 이미지 이름은 필수 입력 입니다.")
    @Size(max = 255, message = "객실 이미지 이름은 255자 이하로 입력해야 합니다.")
    private String roomImageName;

    @NotBlank(message = "객실 이미지 경로는 필수 입력 입니다.")
    @Size(max = 500, message = "객실 이미지 경로는 500자 이하로 입력해야 합니다.")
    private String roomImagePath;

    private LocalDateTime createdAt;
}
