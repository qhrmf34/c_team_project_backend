package com.hotel_project.payment_jpa.coupon.repository;

import com.hotel_project.payment_jpa.coupon.dto.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, Long> {

    // 쿠폰명 중복 체크
    boolean existsByCouponName(String countryName);

    // 쿠폰명 중복 체크 (특정 ID 제외)
    boolean existsByCouponNameAndIdNot(String countryName, Long id);

    // ID 존재 여부 체크
    boolean existsById(Long id);
}