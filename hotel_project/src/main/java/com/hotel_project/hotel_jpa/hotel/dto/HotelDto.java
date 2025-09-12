package com.hotel_project.hotel_jpa.hotel.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.city.dto.CityDto;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDto implements IHotel{
    private Long id;

    @JsonIgnore
    private CityDto cityDto;

    @NotNull
    private Long cityId;

    @NotNull(message = "호텔 타입은 필수 입력 입니다.")
    private HotelType hotelType;

    @NotBlank(message = "호텔 이름은 필수 입력 입니다.")
    @Size(max = 255, message = "호텔 이름은 255자 이하로 입력해야 합니다.")
    private String hotelName;

    @DecimalMax(value = "90.0", inclusive = true, message = "위도는 90이하 이여야 합니다.")
    @DecimalMin(value = "-90.0", inclusive = true, message = "위도는 -90이상 이여야 합니다.")
    @Digits(integer = 3, fraction = 6, message = "위도는 최대 3자리 정수, 6자리 소수까지 가능합니다.")
    private BigDecimal hotelLatitude;

    @DecimalMax(value = "180.0", inclusive = true, message = "경도는 180이하 이여야 합니다.")
    @DecimalMin(value = "-180.0", inclusive = true, message = "경도는 -180이상 이여야 합니다.")
    @Digits(integer = 3, fraction = 8, message = "경도는 최대 3자리 정수, 8자리 소수까지 가능합니다.")
    private BigDecimal hotelLongitude;

    private String hotelContent;

    private Integer hotelStar;

    private Integer freebiesNumber;

    @Size(max = 30, message = "호텔 전화 번호는 30자 이하로 입력해야 합니다.")
    private String hotelNumber;

    @NotNull(message = "체크인 시간은 필수 입력 입니다.")
    private LocalTime checkinTime;

    @NotNull(message = "체크아웃 시간은 필수 입력 입니다.")
    private LocalTime checkoutTime;

    @DecimalMin(value = "1.0", message = "평균 평점은 1.0 이상이어야 합니다.")
    @DecimalMax(value = "5.0", message = "평균 평점은 5.0 이하여야 합니다.")
    @Digits(integer = 1, fraction = 1, message = "별점은 1.0 ~ 5.0 사이에 값을 입력 하셔야 합니다.")
    private BigDecimal hotelRating;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public IId getCity() {
        return this.cityDto;
    }

    @Override
    public void setCity(IId iId){
        if (iId == null){
            return;
        }
        if (this.cityDto == null){
            this.cityDto = new CityDto();
        }
        this.cityDto.copyMembersId(iId);
    }

    @Override
    public Long getCityId() {
        if (this.cityDto != null){
            return this.cityDto.getId();
        }
        return this.cityId;
    }

    @Override
    public void setCityId(Long id) {
        if (id == null){
            if (this.cityDto != null && this.cityDto.getId() != null){
                this.cityDto.setId(this.cityDto.getId());
            }
            return;
        }
        this.cityId = id;
        if (this.cityDto == null){
            this.cityDto = new CityDto();
        }
        this.cityDto.setId(id);
    }
}
