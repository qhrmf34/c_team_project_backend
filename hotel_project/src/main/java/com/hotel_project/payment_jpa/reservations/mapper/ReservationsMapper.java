package com.hotel_project.payment_jpa.reservations.mapper;

import com.hotel_project.payment_jpa.reservations.dto.ReservationHistoryDto;
import com.hotel_project.payment_jpa.reservations.dto.ReservationSummaryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationsMapper {

    // ✅ 페이지네이션 파라미터 추가
    List<ReservationSummaryDto> findReservationsByMemberId(
            @Param("memberId") Long memberId,
            @Param("offset") Integer offset,
            @Param("size") Integer size
    );
    int countReservationHistoryByMemberId(@Param("memberId") Long memberId);

    // ✅ 새로운 메서드 (결제 내역용)
    List<ReservationHistoryDto> findReservationHistoryByMemberId(
            @Param("memberId") Long memberId,
            @Param("offset") Integer offset,
            @Param("size") Integer size
    );
    // ✅ 개수 조회 추가
    int countReservationsByMemberId(@Param("memberId") Long memberId);
}