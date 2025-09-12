package com.hotel_project.hotel_jpa.city_image.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.city.dto.CityDto;
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
public class CityImageDto implements ICityImage{
    private Long id;

    @JsonIgnore
    private CityDto cityDto;

    @NotNull
    private Long cityId;

    @NotBlank(message = "이미지 이름은 필수 입력 입니다.")
    @Size(max = 255, message = "이미지 이름은 255자 이하로 입력해야 합니다.")
    private String cityImageName;

    @NotBlank(message = "이미지 경로는 필수 입력 입니다.")
    @Size(max = 500, message = "이미지 경로는 500자 이하로 입력해야 합니다.")
    private String cityImagePath;

    private Long cityImageSize;

    private Integer cityImageIndex;

    private LocalDateTime createdAt;

    @Override
    public IId getCity(){
        return this.cityDto;
    }

    @Override
    public void setCity(IId iId) {
        if(iId == null){
            return;
        }
        if(this.cityDto == null){
            this.cityDto = new CityDto();
        }
        this.cityDto.copyMembersId(iId);
    }

    @Override
    public Long getCityId() {
        if(this.cityDto != null){
            return this.cityDto.getId();
        }
        return this.cityId;
    }

    @Override
    public void setCityId(Long cityId) {
        if(cityId == null){
            if(this.cityDto != null && this.cityDto.getId() != null){
                this.cityDto.setId(this.cityDto.getId());
            }
            return;
        }
        this.cityId = cityId;
        if(this.cityDto == null){
            this.cityDto = new CityDto();
        }
        this.cityDto.setId(cityId);
    }
}
