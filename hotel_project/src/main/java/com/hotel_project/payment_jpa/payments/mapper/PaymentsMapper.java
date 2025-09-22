// PaymentsMapper.java
package com.hotel_project.payment_jpa.payments.mapper;

import com.hotel_project.payment_jpa.payments.dto.PaymentsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentsMapper {

    // 예약별 결제 내역 조회
    List<PaymentsDto> findByReservationsId(@Param("reservationsId") Long reservationsId);

    // 회원별 결제 내역 조회
    List<PaymentsDto> findByMemberId(@Param("memberId") Long memberId);

    // ID로 단건 조회
    PaymentsDto findById(@Param("id") Long id);

    // 토스 결제 키로 조회
    PaymentsDto findByTossPaymentKey(@Param("tossPaymentKey") String tossPaymentKey);
}