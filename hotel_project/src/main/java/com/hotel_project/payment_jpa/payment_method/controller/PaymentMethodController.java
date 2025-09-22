// PaymentMethodController.java
package com.hotel_project.payment_jpa.payment_method.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
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

    /**
     * 결제수단 등록 API
     */
    @PostMapping("/register")
    @Operation(summary = "결제수단 등록", description = "카드 정보로 결제수단을 등록합니다.")
    public ResponseEntity<ApiResponse<PaymentMethodDto>> registerPaymentMethod(
            @Valid @RequestBody CardRegistrationRequestDto request,
//            @RequestParam Long memberId,
            BindingResult bindingResult
    ) throws CommonExceptionTemplate {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" ")
            );
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }
        Long memberId =1L;
        log.info("결제수단 등록 요청 - 회원 ID: {}", memberId);
        PaymentMethodDto result = paymentMethodService.registerPaymentMethod(request, memberId);

        return ResponseEntity.ok(ApiResponse.success(200, "결제수단이 성공적으로 등록되었습니다", result));
    }

    /**
     * 회원의 결제수단 목록 조회 API
     */
    @GetMapping
    @Operation(summary = "결제수단 목록 조회", description = "회원의 모든 결제수단을 조회합니다.")
    public ResponseEntity<ApiResponse<List<PaymentMethodDto>>> getPaymentMethods(
            @RequestParam Long memberId
    ) throws CommonExceptionTemplate {

        List<PaymentMethodDto> paymentMethods = paymentMethodService.getPaymentMethods(memberId);
        return ResponseEntity.ok(ApiResponse.success(200, "success", paymentMethods));
    }

    /**
     * 결제수단 단건 조회 API
     */
    @GetMapping("/{id}")
    @Operation(summary = "결제수단 단건 조회", description = "ID로 결제수단을 조회합니다.")
    public ResponseEntity<ApiResponse<PaymentMethodDto>> getPaymentMethod(
            @PathVariable Long id
    ) throws CommonExceptionTemplate {

        PaymentMethodDto paymentMethod = paymentMethodService.getPaymentMethod(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", paymentMethod));
    }

    /**
     * 결제수단 삭제 API
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "결제수단 삭제", description = "결제수단을 삭제합니다.")
    public String deletePaymentMethod(
            @PathVariable Long id,
            @RequestParam Long memberId
    ) throws CommonExceptionTemplate {

        return paymentMethodService.deletePaymentMethod(id, memberId);
    }



}