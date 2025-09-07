package com.hotel_project.hotel_jpa.country.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryDto implements ICountry{

    private Long id;

    @NotBlank(message = "나라 이름은 필수 입력입니다.")
    @Size(min = 1, max = 100, message = "나라이름은 1자 이상 100자 이하로 입력해야 합니다.")
    private String countryName;

    @NotBlank(message = "식별 번호는 필수 입력입니다.")
    @Size(min = 1, max = 100, message = "식별 번호는 1자 이상 10자 이하로 입력해야 합니다.")
    private String idd;
}
