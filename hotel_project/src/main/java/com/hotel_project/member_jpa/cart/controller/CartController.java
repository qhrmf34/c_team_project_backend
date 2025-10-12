package com.hotel_project.member_jpa.cart.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.member_jpa.cart.dto.CartDto;
import com.hotel_project.member_jpa.cart.dto.CartToggleRequest;
import com.hotel_project.member_jpa.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carts")
@Tag(name = "Cart API", description = "장바구니 관리 API")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/toggle")
    @Operation(summary = "장바구니 토글", description = "호텔 장바구니 추가/제거")
    public ResponseEntity<ApiResponse<Boolean>> toggleCart(
            @Valid @RequestBody CartToggleRequest request,
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        String token = jwtUtil.extractToken(authorization);

        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.getMemberIdFromToken(token);

        boolean isAdded = cartService.toggle(memberId, request.getHotelId());
        String message = isAdded ? "장바구니에 추가되었습니다." : "장바구니에서 제거되었습니다.";

        log.info("장바구니 토글 완료 - memberId: {}, hotelId: {}, isAdded: {}",
                memberId, request.getHotelId(), isAdded);

        return ResponseEntity.ok(ApiResponse.success(200, message, isAdded));
    }

    @GetMapping
    @Operation(summary = "내 장바구니 조회 (페이지네이션)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyCarts(
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "3") Integer size,
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        String token = jwtUtil.extractToken(authorization);

        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.getMemberIdFromToken(token);

        Map<String, Object> result = cartService.findByMemberIdWithPagination(
                memberId, offset, size);

        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @GetMapping("/all")
    @Operation(summary = "내 장바구니 전체 조회")
    public ResponseEntity<ApiResponse<List<CartDto>>> getAllMyCarts(
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        String token = jwtUtil.extractToken(authorization);

        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.getMemberIdFromToken(token);
        List<CartDto> carts = cartService.findByMemberId(memberId);

        return ResponseEntity.ok(ApiResponse.success(200, "success", carts));
    }

    @DeleteMapping("/{hotelId}")
    @Operation(summary = "장바구니에서 삭제")
    public ResponseEntity<ApiResponse<String>> deleteCart(
            @PathVariable Long hotelId,
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        String token = jwtUtil.extractToken(authorization);

        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.getMemberIdFromToken(token);
        cartService.delete(memberId, hotelId);

        return ResponseEntity.ok(ApiResponse.success(200, "장바구니에서 삭제되었습니다.", null));
    }

    @DeleteMapping("/item/{cartId}")
    @Operation(summary = "장바구니 항목 삭제")
    public ResponseEntity<ApiResponse<String>> deleteCartById(
            @PathVariable Long cartId,
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        String token = jwtUtil.extractToken(authorization);

        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.getMemberIdFromToken(token);
        cartService.deleteById(memberId, cartId);

        return ResponseEntity.ok(ApiResponse.success(200, "장바구니에서 삭제되었습니다.", null));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "장바구니 전체 비우기")
    public ResponseEntity<ApiResponse<String>> clearCart(
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        String token = jwtUtil.extractToken(authorization);

        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.getMemberIdFromToken(token);
        cartService.clearCart(memberId);

        return ResponseEntity.ok(ApiResponse.success(200, "장바구니가 비워졌습니다.", null));
    }

    @GetMapping("/check/{hotelId}")
    @Operation(summary = "장바구니 포함 여부 확인")
    public ResponseEntity<ApiResponse<Boolean>> checkCart(
            @PathVariable Long hotelId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        try {
            if (authorization == null) {
                return ResponseEntity.ok(ApiResponse.success(200, "success", false));
            }

            String token = jwtUtil.extractToken(authorization);

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.ok(ApiResponse.success(200, "success", false));
            }

            Long memberId = jwtUtil.getMemberIdFromToken(token);
            boolean isInCart = cartService.isInCart(memberId, hotelId);

            return ResponseEntity.ok(ApiResponse.success(200, "success", isInCart));
        } catch (Exception e) {
            log.error("장바구니 확인 중 오류 발생", e);
            return ResponseEntity.ok(ApiResponse.success(200, "success", false));
        }
    }

    @GetMapping("/count")
    @Operation(summary = "장바구니 항목 개수")
    public ResponseEntity<ApiResponse<Long>> getCartCount(
            @RequestHeader(value = "Authorization", required = false) String authorization) {

        try {
            if (authorization == null) {
                return ResponseEntity.ok(ApiResponse.success(200, "success", 0L));
            }

            String token = jwtUtil.extractToken(authorization);

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.ok(ApiResponse.success(200, "success", 0L));
            }

            Long memberId = jwtUtil.getMemberIdFromToken(token);
            long count = cartService.getCartCount(memberId);

            return ResponseEntity.ok(ApiResponse.success(200, "success", count));
        } catch (Exception e) {
            log.error("장바구니 개수 조회 중 오류 발생", e);
            return ResponseEntity.ok(ApiResponse.success(200, "success", 0L));
        }
    }
}