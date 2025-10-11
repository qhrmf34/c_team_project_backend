package com.hotel_project.payment_jpa.member_coupon.repository;

import com.hotel_project.payment_jpa.member_coupon.dto.MemberCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberCouponRepository extends JpaRepository<MemberCouponEntity, Long> {


    /**
     * 회원의 특정 쿠폰 소유 여부 확인
     */
    boolean existsByMemberEntity_IdAndCouponEntity_Id(Long memberId, Long couponId);
}