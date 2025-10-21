package com.hotel_project.payment_jpa.reservations.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.payment_jpa.reservations.dto.ReservationsDto;
import com.hotel_project.payment_jpa.reservations.dto.ReservationSummaryDto;
import com.hotel_project.payment_jpa.reservations.service.ReservationsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Reservations API", description = "예약 관리 API")
public class ReservationsController {

    private final ReservationsService reservationsService;
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

    @PostMapping
    @Operation(summary = "예약 생성", description = "새로운 예약을 생성합니다 (결제 전).")
    public ResponseEntity<ApiResponse<ReservationsDto>> createReservation(
            @Valid @RequestBody ReservationsDto reservationsDto,
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

        Long memberId = getMemberIdFromToken(request);
        ReservationsDto result = reservationsService.createReservation(reservationsDto, memberId);

        return ResponseEntity.ok(ApiResponse.success(200, "예약이 생성되었습니다", result));
    }

    @GetMapping("/my")
    @Operation(summary = "내 예약 목록 조회", description = "현재 로그인한 사용자의 모든 예약 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<ReservationSummaryDto>>> getMyReservations(
            HttpServletRequest request
    ) throws CommonExceptionTemplate {
        Long memberId = getMemberIdFromToken(request);
        List<ReservationSummaryDto> reservations = reservationsService.getMyReservations(memberId);

        return ResponseEntity.ok(ApiResponse.success(200, "예약 목록 조회 성공", reservations));
    }
}