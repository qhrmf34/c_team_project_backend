// TicketController.java (새로 생성)
package com.hotel_project.payment_jpa.ticket.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.payment_jpa.ticket.dto.TicketDto;
import com.hotel_project.payment_jpa.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/tickets")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Ticket API", description = "티켓 관리 API")
public class TicketController {

    private final TicketService ticketService;
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
     * ✅ 결제 ID로 티켓 상세 정보 조회
     */
    @GetMapping("/payment/{paymentId}")
    @Operation(summary = "티켓 조회", description = "결제 ID로 티켓 정보를 조회합니다")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTicketByPaymentId(
            @PathVariable Long paymentId,
            HttpServletRequest request
    ) throws CommonExceptionTemplate {

        Long memberId = getMemberIdFromToken(request);
        Map<String, Object> ticketInfo = ticketService.getTicketDetailByPaymentId(paymentId, memberId);

        return ResponseEntity.ok(ApiResponse.success(200, "조회 성공", ticketInfo));
    }
}