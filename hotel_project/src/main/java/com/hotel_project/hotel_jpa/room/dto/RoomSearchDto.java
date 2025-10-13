package com.hotel_project.hotel_jpa.room.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

//객실 검색 요청 DTO HotelThree.vue에서 날짜별 객실 재고 조회 시 사용
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomSearchDto {
    @NotNull(message = "호텔 ID는 필수입니다.")
    private Long hotelId;

    @NotNull(message = "체크인 날짜는 필수입니다.")
    private LocalDate checkIn;

    @NotNull(message = "체크아웃 날짜는 필수입니다.")
    private LocalDate checkOut;

    private Integer guests;
}