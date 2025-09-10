package com.hotel_project.hotel_jpa.room.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDto implements IRoom{
    private Long id;

    private Long hotelId;

    @NotBlank(message = "방 이름은 필수 입력 입니다.")
    @Size(max = 30,message = "방 이름은 30자 이하로 입력해야 합니다.")
    private String roomName;

    private Byte roomSingleBed;

    private Byte roomDoubleBed;

    @NotNull(message = "가격은 필수 입력 입니다.")
    @Digits(integer = 10, fraction = 2, message = "가격은 최대 10자리 정수, 2자리 소수까지 가능합니다.")
    private BigDecimal basePrice;

    @NotNull(message = "룸 번호는 필수 입력 입니다.")
    private Integer roomNumber;

    @Size(max = 30, message = "룸 뷰의 내용은 30자 이하로 입력해야합니다.")
    private String roomView;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
