package com.hotel_project.hotel_jpa.city_image.dto;

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
}
