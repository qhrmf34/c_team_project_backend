// PaymentsController.java
package com.hotel_project.payment_jpa.payments.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.payment_jpa.payments.dto.PaymentRequestDto;
import com.hotel_project.payment_jpa.payments.dto.PaymentsDto;
import com.hotel_project.payment_jpa.payments.service.PaymentsService;
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
@RequestMapping("/api/payments")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Payments API", description = "결제 관리 API")
public class PaymentsController {

    private final PaymentsService paymentsService;

    /**
     * 결제 처리 API
     */
    @PostMapping("/process")
    @Operation(summary = "결제 처리", description = "결제를 처리합니다.")
    public ResponseEntity<ApiResponse<PaymentsDto>> processPayment(
            @Valid @RequestBody PaymentRequestDto request,
            BindingResult bindingResult
    ) throws CommonExceptionTemplate {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" ")
            );
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        log.info("결제 요청 - 예약 ID: {}, 금액: {}", request.getReservationsId(), request.getPaymentAmount());
        PaymentsDto result = paymentsService.processPayment(request);

        return ResponseEntity.ok(ApiResponse.success(200, "결제가 성공적으로 완료되었습니다", result));
    }

    /**
     * 예약별 결제 내역 조회 API
     */
    @GetMapping("/reservation/{reservationsId}")
    @Operation(summary = "예약별 결제 내역 조회", description = "예약별 결제 내역을 조회합니다.")
    public ResponseEntity<ApiResponse<List<PaymentsDto>>> getPaymentsByReservation(
            @PathVariable Long reservationsId
    ) throws CommonExceptionTemplate {

        List<PaymentsDto> payments = paymentsService.getPaymentsByReservation(reservationsId);
        return ResponseEntity.ok(ApiResponse.success(200, "success", payments));
    }

    /**
     * 회원별 결제 내역 조회 API
     */
    @GetMapping("/member/{memberId}")
    @Operation(summary = "회원별 결제 내역 조회", description = "회원별 결제 내역을 조회합니다.")
    public ResponseEntity<ApiResponse<List<PaymentsDto>>> getPaymentsByMember(
            @PathVariable Long memberId
    ) throws CommonExceptionTemplate {

        List<PaymentsDto> payments = paymentsService.getPaymentsByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success(200, "success", payments));
    }

    /**
     * 결제 단건 조회 API
     */
    @GetMapping("/{id}")
    @Operation(summary = "결제 단건 조회", description = "ID로 결제를 조회합니다.")
    public ResponseEntity<ApiResponse<PaymentsDto>> getPayment(
            @PathVariable Long id
    ) throws CommonExceptionTemplate {

        PaymentsDto payment = paymentsService.getPayment(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", payment));
    }
}