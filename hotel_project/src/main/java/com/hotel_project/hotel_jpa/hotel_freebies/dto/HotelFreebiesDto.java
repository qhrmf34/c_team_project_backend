package com.hotel_project.hotel_jpa.hotel_freebies.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import com.hotel_project.hotel_jpa.hotel.dto.HotelDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelFreebiesDto implements IHotelFreebies {
    private Long id;

    @JsonIgnore
    private HotelDto hotelDto;

    @NotNull
    private Long hotelId;

    @JsonIgnore
    private FreebiesDto freebiesDto;

    @NotNull
    private Long freebiesId;

    @NotNull(message = "이용 가능 여부는 필수 입력입니다.")
    private Boolean isAvailable;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public IId getHotel(){
        return this.hotelDto;
    }

    @Override
    public void setHotel(IId iId) {
        if (iId == null) {
            return;
        }
        if (this.hotelDto == null) {
            this.hotelDto = new HotelDto();
        }
        this.hotelDto.copyMembersId(iId);
    }

    @Override
    public Long getHotelId() {
        if (this.hotelDto != null) {
            return this.hotelDto.getId();
        }
        return this.hotelId;
    }

    @Override
    public void setHotelId(Long id) {
        if (id == null) {
            if (this.hotelDto != null && this.hotelDto.getId() != null) {
                this.hotelDto.setId(this.hotelDto.getId());
            }
            return;
        }
        this.hotelId = id;
        if (this.hotelDto == null){
            this.hotelDto = new HotelDto();
        }
        this.hotelDto.setId(id);
    }

    @Override
    public IId getFreebies() {
        return this.freebiesDto;
    }

    @Override
    public void setFreebies(IId iId) {
        if (iId == null) {
            return;
        }
        if (this.freebiesDto == null) {
            this.freebiesDto = new FreebiesDto();
        }
        this.freebiesDto.copyMembersId(iId);
    }

    @Override
    public Long getFreebiesId() {
        if (this.freebiesDto != null) {
            return this.freebiesDto.getId();
        }
        return this.freebiesId;
    }

    @Override
    public void setFreebiesId(Long id) {
        if (id == null) {
            if (this.freebiesDto != null && this.freebiesDto.getId() != null) {
                this.freebiesDto.setId(this.freebiesDto.getId());
            }
            return;
        }
        this.freebiesId = id;
        if (this.freebiesDto == null){
            this.freebiesDto = new FreebiesDto();
        }
        this.freebiesDto.setId(id);
    }
}
