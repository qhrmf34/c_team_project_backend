package com.hotel_project.member_jpa.cart.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.hotel.dto.HotelDto;
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
    private Long hotelId;

    @NotNull
    private HotelDto hotelDto;

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
    public IId getHotel(){
        return this.hotelDto;
    }

    @Override
    public void setHotel(IId iId) {
        if(iId == null){
            return;
        }
        if(this.hotelDto == null){
            this.hotelDto = new HotelDto();
        }
        this.hotelDto.copyMembersId(iId);
    }

    @Override
    public Long getHotelId(){
        if (this.hotelDto != null){
            return this.hotelDto.getId();
        }
        return this.hotelId;
    }

    @Override
    public void setHotelId(Long hotelId) {
        if(hotelId == null){
            if(this.hotelDto != null && this.hotelDto.getId() != null){
                this.hotelDto.setId(this.hotelDto.getId());
            }
            return;
        }
        this.hotelId = hotelId;
        if(this.hotelDto == null){
            this.hotelDto = new HotelDto();
        }
        this.hotelDto.setId(hotelId);
    }
}
