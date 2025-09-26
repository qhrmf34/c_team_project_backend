package com.hotel_project.payment_jpa.payment_method.mapper;

import com.hotel_project.payment_jpa.payment_method.dto.PaymentMethodDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentMethodMapper {

    // 회원별 결제수단 조회
    List<PaymentMethodDto> findByMemberId(@Param("memberId") Long memberId);

    // ID로 단건 조회
    PaymentMethodDto findById(@Param("id") Long id);

    // 토스키로 조회
    PaymentMethodDto findByTossKey(@Param("tossKey") String tossKey);
}
