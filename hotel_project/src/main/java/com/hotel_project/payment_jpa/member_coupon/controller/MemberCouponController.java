package com.hotel_project.payment_jpa.member_coupon.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.member_jpa.member.dto.MemberDto;
import com.hotel_project.member_jpa.member.service.MemberService;
import com.hotel_project.payment_jpa.coupon.dto.CouponViewDto;
import com.hotel_project.payment_jpa.member_coupon.service.MemberCouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/member-coupons")
@RequiredArgsConstructor
@Tag(name = "Member Coupon API", description = "회원 쿠폰 관리 API")
public class MemberCouponController {

    private final MemberCouponService memberCouponService;
    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    @PostMapping("/subscribe")
    @Operation(summary = "구독 쿠폰 지급", description = "로그인한 회원에게 모든 활성 쿠폰을 지급합니다.")
    public ResponseEntity<ApiResponse<List<CouponViewDto>>> subscribeAndReceiveCoupons(
            @RequestHeader("Authorization") String authorization
    ) throws CommonExceptionTemplate {
        String token = jwtUtil.extractToken(authorization);
        MemberDto member = memberService.getMemberDtoByToken(token);

        List<CouponViewDto> coupons = memberCouponService.giveAllActiveCouponsToMember(member.getId());

        return ResponseEntity.ok(ApiResponse.success(
                200,
                "구독이 완료되었습니다! " + coupons.size() + "개의 쿠폰이 지급되었습니다.",
                coupons
        ));
    }

    @GetMapping("/my")
    @Operation(summary = "내 쿠폰 목록 조회", description = "로그인한 회원의 쿠폰 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<CouponViewDto>>> getMyCoupons(
            @RequestHeader("Authorization") String authorization
    ) throws CommonExceptionTemplate {
        String token = jwtUtil.extractToken(authorization);
        MemberDto member = memberService.getMemberDtoByToken(token);

        List<CouponViewDto> coupons = memberCouponService.getMemberCoupons(member.getId());

        return ResponseEntity.ok(ApiResponse.success(
                200,
                "쿠폰 목록 조회 완료",
                coupons
        ));
    }
}