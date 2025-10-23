package com.hotel_project.payment_jpa.member_coupon.repository;

import com.hotel_project.payment_jpa.member_coupon.dto.MemberCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberCouponRepository extends JpaRepository<MemberCouponEntity, Long> {


    /**
     * 회원의 특정 쿠폰 소유 여부 확인
     */
    boolean existsByMemberEntity_IdAndCouponEntity_Id(Long memberId, Long couponId);
    /**
     * 회원의 미사용 쿠폰 조회
     */
    Optional<MemberCouponEntity> findByMemberEntity_IdAndCouponEntity_IdAndIsUsedFalse(Long memberId, Long couponId);

}