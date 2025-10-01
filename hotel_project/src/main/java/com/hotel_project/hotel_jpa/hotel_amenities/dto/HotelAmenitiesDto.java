package com.hotel_project.hotel_jpa.hotel_amenities.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesDto;
import com.hotel_project.hotel_jpa.hotel.dto.HotelDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelAmenitiesDto implements IHotelAmenities{
    private Long id;

    @JsonIgnore
    private HotelDto hotelDto;

    @NotNull
    private Long hotelId;

    @JsonIgnore
    private AmenitiesDto amenitiesDto;

    @NotNull
    private Long amenitiesId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public IId getHotel(){
        return this.hotelDto;
    }

    @Override
    public  void setHotel(IId iId){
        if (iId == null){
            return;
        }
        if (this.hotelDto == null){
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
    public void setHotelId(Long id) {
        if (id == null){
            if (this.hotelDto != null && this.hotelDto.getId() != null){
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
    public IId getAmenities(){
        return this.amenitiesDto;
    }

    @Override
    public void setAmenities(IId iId) {
        if (iId == null){
            return;
        }
        if (this.amenitiesDto == null){
            this.amenitiesDto = new AmenitiesDto();
        }
        this.amenitiesDto.copyMembersId(iId);
    }

    @Override
    public Long getAmenitiesId(){
        if(this.hotelDto != null){
            return this.amenitiesDto.getId();
        }
        return this.amenitiesId;
    }

    @Override
    public void setAmenitiesId(Long id) {
        if (id == null){
            if (this.amenitiesDto != null && this.amenitiesDto.getId() != null){
                this.amenitiesDto.setId(this.amenitiesDto.getId());
            }
            return;
        }
        this.amenitiesId = id;
        if (this.amenitiesDto == null){
            this.amenitiesDto = new AmenitiesDto();
        }
        this.amenitiesDto.setId(id);
    }
}
