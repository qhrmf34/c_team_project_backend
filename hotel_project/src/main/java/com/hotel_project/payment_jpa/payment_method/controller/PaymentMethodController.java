// PaymentMethodController.java
package com.hotel_project.payment_jpa.payment_method.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.payment_jpa.payment_method.dto.CardRegistrationRequestDto;
import com.hotel_project.payment_jpa.payment_method.dto.PaymentMethodDto;
import com.hotel_project.payment_jpa.payment_method.service.PaymentMethodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payment-methods")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "PaymentMethod API", description = "결제수단 관리 API")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;
    private final JwtUtil jwtUtil;

    /**
     * JWT 토큰에서 회원 ID 추출
     */
    private Long getMemberIdFromToken(HttpServletRequest request) throws CommonExceptionTemplate {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CommonExceptionTemplate(401, "인증 토큰이 필요합니다");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다");
        }

        return jwtUtil.getMemberIdFromToken(token);
    }

    /**
     * 결제수단 등록 API (토큰 기반)
     */
    @PostMapping("/register")
    @Operation(summary = "결제수단 등록", description = "카드 정보로 결제수단을 등록합니다.")
    public ResponseEntity<ApiResponse<PaymentMethodDto>> registerPaymentMethod(
            @Valid @RequestBody CardRegistrationRequestDto request,
            HttpServletRequest httpRequest,
            BindingResult bindingResult
    ) throws CommonExceptionTemplate {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" ")
            );
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        Long memberId = getMemberIdFromToken(httpRequest);
        log.info("결제수단 등록 요청 - 회원 ID: {}", memberId);

        PaymentMethodDto result = paymentMethodService.registerPaymentMethod(request, memberId);

        return ResponseEntity.ok(ApiResponse.success(200, "결제수단이 성공적으로 등록되었습니다", result));
    }

    /**
     * 내 결제수단 목록 조회 API (토큰 기반) - 순서 변경
     */
    @GetMapping("/my")
    @Operation(summary = "내 결제수단 목록 조회", description = "토큰으로 인증된 회원의 모든 결제수단을 조회합니다.")
    public ResponseEntity<ApiResponse<List<PaymentMethodDto>>> getMyPaymentMethods(
            HttpServletRequest request
    ) throws CommonExceptionTemplate {

        Long memberId = getMemberIdFromToken(request);
        log.info("결제수단 목록 조회 - 회원 ID: {}", memberId);

        List<PaymentMethodDto> paymentMethods = paymentMethodService.getPaymentMethods(memberId);
        return ResponseEntity.ok(ApiResponse.success(200, "조회 성공", paymentMethods));
    }

    /**
     * 결제수단 삭제 API (토큰 기반)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "결제수단 삭제", description = "결제수단을 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> deletePaymentMethod(
            @PathVariable Long id,
            HttpServletRequest request
    ) throws CommonExceptionTemplate {

        Long memberId = getMemberIdFromToken(request);
        log.info("결제수단 삭제 요청 - 회원 ID: {}, 결제수단 ID: {}", memberId, id);

        String result = paymentMethodService.deletePaymentMethod(id, memberId);
        return ResponseEntity.ok(ApiResponse.success(200, "결제수단이 삭제되었습니다", result));
    }

    /**
     * 결제수단 단건 조회 API - /my 다음에 배치
     */
    @GetMapping("/{id}")
    @Operation(summary = "결제수단 단건 조회", description = "ID로 결제수단을 조회합니다.")
    public ResponseEntity<ApiResponse<PaymentMethodDto>> getPaymentMethod(
            @PathVariable Long id,
            HttpServletRequest request
    ) throws CommonExceptionTemplate {

        Long memberId = getMemberIdFromToken(request);
        PaymentMethodDto paymentMethod = paymentMethodService.getPaymentMethod(id);

        // 본인의 결제수단인지 확인
        if (!paymentMethod.getMemberId().equals(memberId)) {
            throw new CommonExceptionTemplate(403, "접근 권한이 없습니다");
        }

        return ResponseEntity.ok(ApiResponse.success(200, "조회 성공", paymentMethod));
    }

    /**
     * [개발용] 회원별 결제수단 조회 API (파라미터 기반)
     */
    @GetMapping
    @Operation(summary = "[개발용] 회원별 결제수단 조회", description = "회원 ID로 결제수단을 조회합니다.")
    public ResponseEntity<ApiResponse<List<PaymentMethodDto>>> getPaymentMethodsByMemberId(
            @RequestParam Long memberId
    ) throws CommonExceptionTemplate {

        List<PaymentMethodDto> paymentMethods = paymentMethodService.getPaymentMethods(memberId);
        return ResponseEntity.ok(ApiResponse.success(200, "조회 성공", paymentMethods));
    }
}