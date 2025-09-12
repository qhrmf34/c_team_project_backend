package com.hotel_project.hotel_jpa.room_image.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.room.dto.RoomDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @JsonIgnore
    private RoomDto roomDto;

    @NotNull
    private Long roomId;

    @NotBlank(message = "객실 이미지 이름은 필수 입력 입니다.")
    @Size(max = 255, message = "객실 이미지 이름은 255자 이하로 입력해야 합니다.")
    private String roomImageName;

    @NotBlank(message = "객실 이미지 경로는 필수 입력 입니다.")
    @Size(max = 500, message = "객실 이미지 경로는 500자 이하로 입력해야 합니다.")
    private String roomImagePath;

    private Long roomImageSize;

    private LocalDateTime createdAt;

    @Override
    public IId getRoom(){
        return this.roomDto;
    }

    @Override
    public void setRoom(IId iId) {
        if (iId == null) {
            return;
        }
        if(this.roomDto == null) {
            this.roomDto = new RoomDto();
        }
        this.roomDto.copyMembersId(iId);
    }

    @Override
    public Long getRoomId() {
        if(this.roomDto != null) {
            return this.roomDto.getId();
        }
        return this.roomId;
    }

    @Override
    public void setRoomId(Long id) {
        if (id == null) {
            if (this.roomDto != null && this.roomDto.getId() != null) {
                this.roomDto.setId(this.roomDto.getId());
            }
            return;
        }
        this.roomId = id;
        if (this.roomDto == null){
            this.roomDto = new RoomDto();
        }
        this.roomDto.setId(id);
    }
}
