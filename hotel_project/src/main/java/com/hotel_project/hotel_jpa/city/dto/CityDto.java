package com.hotel_project.hotel_jpa.city.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityDto implements ICity{

    private Long id;

    @NotBlank(message = "도시 이름은 필수 입력입니다.")
    @Size(min = 1, max = 100, message = "도시이름은 1자 이상 100자 이하로 입력해야 합니다.")
    private String cityName;

    private String cityContent;

    @NotBlank(message = "식별 번호는 필수 입력입니다.")
    @Size(min = 1, max = 100, message = "식별 번호는 1자 이상 10자 이하로 입력해야 합니다.")
    private String idd;
}
