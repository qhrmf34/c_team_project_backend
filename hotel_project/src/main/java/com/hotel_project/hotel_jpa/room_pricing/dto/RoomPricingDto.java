package com.hotel_project.hotel_jpa.room_pricing.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.room.dto.RoomDto;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomPricingDto implements IRoomPricing{
    private Long id;

    @JsonIgnore
    private RoomDto roomDto;

    @NotNull
    private Long roomId;

    @NotNull(message = "해당일은 필수 입력 입니다.")
    private LocalDate date;

    @NotNull(message = "가격은 필수 입력 입니다.")
    private Long price;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public IId getRoom(){
        return this.roomDto;
    }

    @Override
    public void setRoom(IId iId) {
        if(iId == null){
            return;
        }
        if(this.roomDto == null){
            this.roomDto = new RoomDto();
        }
        this.roomDto.copyMembersId(iId);
    }

    @Override
    public Long getRoomId() {
        if (this.roomDto != null) {
            return this.roomDto.getId();
        }
        return this.roomId;
    }

    @Override
    public void setRoomId(Long id) {
        if(id == null){
            if(this.roomDto != null && this.roomDto.getId() != null){
                this.roomDto.setId(this.roomDto.getId());
            }
            return;
        }
        this.roomId = id;
        if(this.roomDto == null){
            this.roomDto = new RoomDto();
        }
        this.roomDto.setId(id);
    }
}
