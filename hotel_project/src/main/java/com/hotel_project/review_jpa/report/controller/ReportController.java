package com.hotel_project.review_jpa.report.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.review_jpa.report.dto.ReportDto;
import com.hotel_project.review_jpa.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Report API", description = "리뷰 신고 API")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    @Operation(summary = "리뷰 신고", description = "본인 리뷰 제외 + 중복 신고 방지")
    public ResponseEntity<ApiResponse<ReportDto>> createReport(
            @Valid @RequestBody ReportDto reportDto,
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        String token = jwtUtil.extractToken(authorization);

        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.getMemberIdFromToken(token);
        reportDto.setMemberId(memberId);

        // 본인 리뷰 체크 포함된 메서드 사용
        ReportDto created = reportService.createReportWithValidation(reportDto);

        log.info("리뷰 신고 완료 - memberId: {}, reviewId: {}, reportId: {}",
                memberId, reportDto.getReviewsId(), created.getId());

        return ResponseEntity.ok(ApiResponse.success(200, "신고가 접수되었습니다.", created));
    }
}