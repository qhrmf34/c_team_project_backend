package com.hotel_project.member_jpa.cart.mapper;

import com.hotel_project.member_jpa.cart.dto.CartDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {

    // 장바구니 조회 (회원별) - 상세 정보 포함
    List<CartDto> findByMemberId(@Param("memberId") Long memberId);

    // 장바구니 존재 여부 확인
    int existsByMemberIdAndRoomId(@Param("memberId") Long memberId,
                                  @Param("roomId") Long roomId);

    // 장바구니 개수 조회
    int countByMemberId(@Param("memberId") Long memberId);

    // 객실 ID 목록 조회 (회원별)
    List<Long> findRoomIdsByMemberId(@Param("memberId") Long memberId);
}