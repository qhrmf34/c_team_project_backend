package com.hotel_project.payment_jpa.refund_reason.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.payment_jpa.refund_reason.dto.RefundReasonDto;
import com.hotel_project.payment_jpa.refund_reason.dto.RefundReasonType;
import com.hotel_project.payment_jpa.refund_reason.service.RefundReasonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/refund-reasons")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Refund Reason API", description = "환불 사유 관리 API")
public class RefundReasonController {

    private final RefundReasonService refundReasonService;
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

    /**
     * 환불 사유 저장
     */
    @PostMapping
    @Operation(summary = "환불 사유 저장", description = "결제 환불 시 사유를 저장합니다.")
    public ResponseEntity<ApiResponse<RefundReasonDto>> saveRefundReason(
            @Valid @RequestBody RefundReasonDto refundReasonDto,
            HttpServletRequest request,
            BindingResult bindingResult
    ) throws CommonExceptionTemplate {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" ")
            );
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        // 토큰 검증 (사용자 인증)
        getMemberIdFromToken(request);

        RefundReasonDto result = refundReasonService.saveRefundReason(refundReasonDto);

        return ResponseEntity.ok(ApiResponse.success(200, "환불 사유가 저장되었습니다", result));
    }

    /**
     * 특정 결제의 환불 사유 조회
     */
    @GetMapping("/payment/{paymentId}")
    @Operation(summary = "결제별 환불 사유 조회", description = "특정 결제의 환불 사유를 조회합니다.")
    public ResponseEntity<ApiResponse<RefundReasonDto>> getRefundReasonByPaymentId(
            @PathVariable Long paymentId,
            HttpServletRequest request
    ) throws CommonExceptionTemplate {

        getMemberIdFromToken(request);

        RefundReasonDto result = refundReasonService.getRefundReasonByPaymentId(paymentId);

        return ResponseEntity.ok(ApiResponse.success(200, "조회 성공", result));
    }

    /**
     * 특정 결제의 모든 환불 사유 조회
     */
    @GetMapping("/payment/{paymentId}/all")
    @Operation(summary = "결제별 전체 환불 사유 조회", description = "특정 결제의 모든 환불 사유를 조회합니다.")
    public ResponseEntity<ApiResponse<List<RefundReasonDto>>> getAllRefundReasonsByPaymentId(
            @PathVariable Long paymentId,
            HttpServletRequest request
    ) throws CommonExceptionTemplate {

        getMemberIdFromToken(request);

        List<RefundReasonDto> results = refundReasonService.getAllRefundReasonsByPaymentId(paymentId);

        return ResponseEntity.ok(ApiResponse.success(200, "조회 성공", results));
    }

    /**
     * 환불 사유 옵션 목록 조회
     */
    @GetMapping("/options")
    @Operation(summary = "환불 사유 옵션 조회", description = "선택 가능한 환불 사유 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getRefundReasonOptions() {

        List<Map<String, String>> options = Arrays.stream(RefundReasonType.values())
                .map(type -> Map.of(
                        "value", type.name(),
                        "label", type.getDescription()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(200, "조회 성공", options));
    }

    /**
     * 환불 사유 ID로 조회
     */
    @GetMapping("/{id}")
    @Operation(summary = "환불 사유 조회", description = "환불 사유 ID로 조회합니다.")
    public ResponseEntity<ApiResponse<RefundReasonDto>> getRefundReasonById(
            @PathVariable Long id,
            HttpServletRequest request
    ) throws CommonExceptionTemplate {

        getMemberIdFromToken(request);

        RefundReasonDto result = refundReasonService.getRefundReasonById(id);

        return ResponseEntity.ok(ApiResponse.success(200, "조회 성공", result));
    }

    /**
     * 모든 환불 사유 조회
     */
    @GetMapping
    @Operation(summary = "전체 환불 사유 조회", description = "모든 환불 사유를 조회합니다.")
    public ResponseEntity<ApiResponse<List<RefundReasonDto>>> getAllRefundReasons(
            HttpServletRequest request
    ) throws CommonExceptionTemplate {

        getMemberIdFromToken(request);

        List<RefundReasonDto> results = refundReasonService.getAllRefundReasons();

        return ResponseEntity.ok(ApiResponse.success(200, "조회 성공", results));
    }
}