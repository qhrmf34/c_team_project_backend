package com.hotel_project.hotel_jpa.city.dto;

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

    @NotNull
    private Long countryId;

    @NotBlank(message = "도시 이름은 필수 입력 입니다.")
    @Size(max = 100, message = "도시 이름은 100자 이하로 입력해야 합니다.")
    private String cityName;

    private String cityContent;

}
