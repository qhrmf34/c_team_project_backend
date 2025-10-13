package com.hotel_project.payment_jpa.payments.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.payment_jpa.payments.dto.PaymentsDto;
import com.hotel_project.payment_jpa.payments.service.PaymentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Payments API", description = "결제 관리 API")
public class PaymentsController {

    private final PaymentsService paymentsService;
    private final JwtUtil jwtUtil;

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

    @PostMapping("/process")
    @Operation(summary = "결제 처리", description = "예약에 대한 결제를 처리합니다.")
    public ResponseEntity<ApiResponse<PaymentsDto>> processPayment(
            @Valid @RequestBody PaymentsDto paymentsDto,
            HttpServletRequest request,
            BindingResult bindingResult
    ) throws CommonExceptionTemplate {
        PaymentsDto a=paymentsDto;
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" ")
            );
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        Long memberId = getMemberIdFromToken(request);

        PaymentsDto result = paymentsService.processPayment(paymentsDto, memberId);

        return ResponseEntity.ok(ApiResponse.success(200, "결제가 완료되었습니다", result));
    }
    // ✅ 새로 추가할 엔드포인트
    @PostMapping("/confirm")
    @Operation(summary = "결제 승인", description = "결제위젯으로 결제 후 승인 처리")
    public ResponseEntity<ApiResponse<PaymentsDto>> confirmPayment(
            @RequestBody Map<String, Object> confirmData,
            HttpServletRequest request
    ) throws CommonExceptionTemplate {

        Long memberId = getMemberIdFromToken(request);

        String paymentKey = (String) confirmData.get("paymentKey");
        String orderId = (String) confirmData.get("orderId");
        Long amount = ((Number) confirmData.get("amount")).longValue();
        Long reservationId = ((Number) confirmData.get("reservationId")).longValue();

        // paymentMethodId는 optional (토스가 결제 처리)
        Object paymentMethodIdObj = confirmData.get("paymentMethodId");
        Long paymentMethodId = (paymentMethodIdObj != null && !"".equals(paymentMethodIdObj)) ?
                ((Number) paymentMethodIdObj).longValue() : null;

        Object couponIdObj = confirmData.get("couponId");
        Long couponId = (couponIdObj != null && !"".equals(couponIdObj)) ?
                ((Number) couponIdObj).longValue() : null;


        PaymentsDto result = paymentsService.confirmWidgetPayment(
                paymentKey, orderId, amount, reservationId, paymentMethodId, couponId, memberId
        );

        return ResponseEntity.ok(ApiResponse.success(200, "결제가 완료되었습니다", result));
    }
}