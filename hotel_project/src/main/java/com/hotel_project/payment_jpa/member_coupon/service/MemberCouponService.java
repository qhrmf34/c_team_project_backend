package com.hotel_project.payment_jpa.member_coupon.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.payment_jpa.coupon.dto.CouponEntity;
import com.hotel_project.payment_jpa.coupon.dto.CouponViewDto;
import com.hotel_project.payment_jpa.coupon.repository.CouponRepository;
import com.hotel_project.payment_jpa.member_coupon.dto.MemberCouponEntity;
import com.hotel_project.payment_jpa.member_coupon.mapper.MemberCouponMapper;
import com.hotel_project.payment_jpa.member_coupon.repository.MemberCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCouponService {

    private final MemberCouponRepository memberCouponRepository;
    private final CouponRepository couponRepository;
    private final MemberCouponMapper memberCouponMapper;

    /**
     * 회원에게 모든 활성 쿠폰을 지급
     */
    public List<CouponViewDto> giveAllActiveCouponsToMember(Long memberId) throws CommonExceptionTemplate {
        if (memberId == null) {
            throw new CommonExceptionTemplate(400, "회원 ID가 필요합니다.");
        }

        // 1. 모든 활성 쿠폰 조회 (만료일이 지나지 않은 것만)
        LocalDate today = LocalDate.now();
        List<CouponEntity> activeCoupons = couponRepository.findByIsActiveTrueAndLastDateGreaterThanEqual(today);

        if (activeCoupons.isEmpty()) {
            throw new CommonExceptionTemplate(404, "지급 가능한 쿠폰이 없습니다.");
        }

        // 2. 이미 가지고 있는 쿠폰 ID 목록 조회 (MyBatis Mapper 사용)
        List<Long> existingCouponIds = memberCouponMapper.findCouponIdsByMemberId(memberId);

        // 3. 아직 가지고 있지 않은 쿠폰만 필터링
        List<CouponEntity> newCoupons = activeCoupons.stream()
                .filter(coupon -> !existingCouponIds.contains(coupon.getId()))
                .collect(Collectors.toList());

        // 4. 새로운 쿠폰 지급
        for (CouponEntity coupon : newCoupons) {
            MemberCouponEntity memberCoupon = new MemberCouponEntity();
            memberCoupon.setMemberId(memberId);
            memberCoupon.setCouponId(coupon.getId());
            memberCoupon.setIsUsed(false);
            memberCouponRepository.save(memberCoupon);
        }

        // 5. 전체 쿠폰 목록 반환 (기존 + 신규) (MyBatis Mapper 사용)
        return getMemberCoupons(memberId);
    }

    /**
     * 회원의 쿠폰 목록 조회
     */
    public List<CouponViewDto> getMemberCoupons(Long memberId) throws CommonExceptionTemplate {
        if (memberId == null) {
            throw new CommonExceptionTemplate(400, "회원 ID가 필요합니다.");
        }

        return memberCouponMapper.findCouponsByMemberId(memberId);
    }
}