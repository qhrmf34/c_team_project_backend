package com.hotel_project.hotel_jpa.hotel_image;

import jakarta.validation.constraints.NotBlank;
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
}
