package com.hotel_project.payment_jpa.reservations.mapper;

import com.hotel_project.payment_jpa.reservations.dto.ReservationSummaryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationsMapper {

    /**
     * 회원의 예약 목록 조회 (호텔, 객실, 도시, 국가 정보 포함)
     */
    List<ReservationSummaryDto> findReservationsByMemberId(@Param("memberId") Long memberId);
}