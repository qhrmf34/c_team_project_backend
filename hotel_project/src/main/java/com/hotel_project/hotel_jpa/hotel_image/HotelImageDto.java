package com.hotel_project.hotel_jpa.hotel_image;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.hotel.dto.HotelDto;
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
public class HotelImageDto implements IHotelImage{
    private Long id;

    @JsonIgnore
    private HotelDto hotelDto;

    @NotNull
    private Long hotelId;

    @NotBlank(message = "호텔 이미지 이름은 필수 입력 입니다.")
    @Size(max = 255, message = "호텔 이미지 이름은 255자 이하로 입력해야 합니다.")
    private String hotelImageName;

    @NotBlank(message = "호텔 이미지 경로는 필수 입력 입니다.")
    @Size(max = 500, message = "호텔 이미지 경로는 500자 이하로 입력해야 합니다.")
    private String hotelImagePath;

    private Long hotelImageSize;

    private Integer hotelImageIndex;

    private LocalDateTime createdAt;

    @Override
    public IId getHotel(){
        return this.hotelDto;
    }

    @Override
    public void setHotel(IId iId){
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
        if(this.hotelDto == null){
            this.hotelDto = new HotelDto();
        }
        this.hotelDto.setId(id);
    }
}
