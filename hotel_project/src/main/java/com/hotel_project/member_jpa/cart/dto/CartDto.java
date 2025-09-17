package com.hotel_project.member_jpa.cart.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.room.dto.RoomDto;
import com.hotel_project.member_jpa.member.dto.MemberDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDto implements ICart {
    private Long id;

    @JsonIgnore
    private Long memberId;

    @NotNull
    private MemberDto memberDto;

    @JsonIgnore
    private Long roomId;

    @NotNull
    private RoomDto roomDto;

    private LocalDateTime createdAt;

    @Override
    public IId getMember(){
        return this.memberDto;
    }

    @Override
    public void setMember(IId iId) {
        if(iId == null){
            return;
        }
        if(this.memberDto == null){
            this.memberDto = new MemberDto();
        }
        this.memberDto.copyMembersId(iId);
    }

    @Override
    public Long getMemberId(){
        if (this.memberDto != null){
            return this.memberDto.getId();
        }
        return this.memberId;
    }

    @Override
    public void setMemberId(Long memberId) {
        if(memberId == null){
            if(this.memberDto != null && this.memberDto.getId() != null){
                this.memberDto.setId(this.memberDto.getId());
            }
            return;
        }
        this.memberId = memberId;
        if(this.memberDto == null){
            this.memberDto = new MemberDto();
        }
        this.memberDto.setId(memberId);
    }

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
    public Long getRoomId(){
        if (this.roomDto != null){
            return this.roomDto.getId();
        }
        return this.roomId;
    }

    @Override
    public void setRoomId(Long roomId) {
        if(roomId == null){
            if(this.roomDto != null && this.roomDto.getId() != null){
                this.roomDto.setId(this.roomDto.getId());
            }
            return;
        }
        this.roomId = roomId;
        if(this.roomDto == null){
            this.roomDto = new RoomDto();
        }
        this.roomDto.setId(roomId);
    }
}
