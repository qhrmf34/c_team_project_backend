package com.hotel_project.hotel_jpa.amenities.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmenitiesDto implements IAmenities{

    private Long id;

    @NotBlank(message = "편의시설 이름은 필수 입력입니다.")
    @Size(min = 1, max = 100, message = "편의시설 이름은 1자 이상 100자 이하로 입력해야 합니다.")
    private String amenitiesName;
}
