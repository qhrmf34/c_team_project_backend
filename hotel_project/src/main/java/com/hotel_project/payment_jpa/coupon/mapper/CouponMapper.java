package com.hotel_project.payment_jpa.coupon.mapper;

import com.hotel_project.payment_jpa.coupon.dto.CouponDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponMapper {

    // 이름 검색 통합 (페이지네이션, 검색어 없으면 전체 조회)
    List<CouponDto> findByName(@Param("CouponName") String countryName,
                               @Param("offset") long offset,
                               @Param("size") int size);

    // 개수 조회 (검색어 없으면 전체 개수)
    long countByName(@Param("CouponName") String countryName);
}
