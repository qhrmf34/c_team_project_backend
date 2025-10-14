package com.hotel_project.payment_jpa.coupon.repository;

import com.hotel_project.payment_jpa.coupon.dto.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<CouponEntity, Long> {

    // 쿠폰명 중복 체크
    boolean existsByCouponName(String couponName);

    // 쿠폰명 중복 체크 (특정 ID 제외)
    boolean existsByCouponNameAndIdNot(String couponName, Long id);

    // ID 존재 여부 체크
    boolean existsById(Long id);

    // 활성 쿠폰 조회 (만료일이 오늘 이후인 것만)
    List<CouponEntity> findByIsActiveTrueAndLastDateGreaterThanEqual(LocalDate date);
}