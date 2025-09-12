package com.hotel_project.hotel_jpa.city.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.country.dto.CountryDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityDto implements ICity{
    private Long id;

    @JsonIgnore
    private CountryDto countryDto;

    @NotNull
    private Long countryId;

    @NotBlank(message = "도시 이름은 필수 입력 입니다.")
    @Size(max = 100, message = "도시 이름은 100자 이하로 입력해야 합니다.")
    private String cityName;

    private String cityContent;

    @Override
    public IId getCountry(){
        return this.countryDto;
    }

    @Override
    public void setCountry(IId iId){
        if(iId == null){
            return;
        }
        if (this.countryDto==null){
            this.countryDto = new CountryDto();
        }
        this.countryDto.copyMembersId(iId);
    }

    @Override
    public Long getCountryId(){
        if(this.countryDto != null){
            return this.countryDto.getId();
        }
        return this.countryId;
    }

    @Override
    public void setCountryId(Long countryId){
        if (countryId == null){
            if (this.countryDto != null && this.countryDto.getId() != null) {
                this.countryDto.setId(this.countryDto.getId());
            }
            return;
        }
        this.countryId = countryId;
        if (this.countryDto == null){
            this.countryDto = new CountryDto();
        }
        this.countryDto.setId(countryId);
    }
}
