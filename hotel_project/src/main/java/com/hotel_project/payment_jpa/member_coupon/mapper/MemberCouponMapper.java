package com.hotel_project.payment_jpa.member_coupon.mapper;

import com.hotel_project.payment_jpa.coupon.dto.CouponViewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberCouponMapper {

    /**
     * 회원의 쿠폰 목록 조회 (사용 여부 포함)
     */
    List<CouponViewDto> findCouponsByMemberId(@Param("memberId") Long memberId);

    /**
     * 회원이 가지고 있는 쿠폰 ID 목록 조회
     */
    List<Long> findCouponIdsByMemberId(@Param("memberId") Long memberId);
}