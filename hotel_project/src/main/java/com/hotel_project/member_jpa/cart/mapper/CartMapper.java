package com.hotel_project.member_jpa.cart.mapper;

import com.hotel_project.member_jpa.cart.dto.CartDto;
import com.hotel_project.member_jpa.cart.dto.WishlistHotelDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {
    List<CartDto> findByMemberId(
            @Param("memberId") Long memberId,
            @Param("offset") Integer offset,
            @Param("size") Integer size
    );
    //  찜 목록 조회용 (WishlistHotelDto)
    List<WishlistHotelDto> findWishlistByMemberId(
            @Param("memberId") Long memberId,
            @Param("offset") Integer offset,
            @Param("size") Integer size
    );
    // 장바구니 존재 여부 확인
    int existsByMemberIdAndHotelId(
            @Param("memberId") Long memberId,
            @Param("hotelId") Long hotelId
    );

    // 장바구니 개수 조회 (totalCount용)
    int countByMemberId(@Param("memberId") Long memberId);

    // 호텔 ID 목록 조회 (회원별)
    List<Long> findHotelIdsByMemberId(@Param("memberId") Long memberId);
}