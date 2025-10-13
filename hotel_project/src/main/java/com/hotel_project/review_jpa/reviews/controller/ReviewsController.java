package com.hotel_project.review_jpa.reviews.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.review_jpa.reviews.dto.ReviewCard;
import com.hotel_project.review_jpa.reviews.dto.ReviewEligibilityDto;
import com.hotel_project.review_jpa.reviews.dto.ReviewsDto;
import com.hotel_project.review_jpa.reviews.service.ReviewsService;
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
@RequestMapping("/api/reviews")
@Tag(name = "Reviews API", description = "리뷰 관리 API")
@Slf4j
public class ReviewsController {

    @Autowired
    private ReviewsService reviewsService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "특정 호텔의 리뷰 조회")
    public ResponseEntity<ApiResponse<List<ReviewsDto>>> getHotelReviews(
            @PathVariable Long hotelId) {

        List<ReviewsDto> reviews = reviewsService.getReviewsByHotelId(hotelId);
        return ResponseEntity.ok(ApiResponse.success(200, "success", reviews));
    }

    @GetMapping("/hotel/{hotelId}/filter")
    @Operation(summary = "필터링된 리뷰 조회")
    public ResponseEntity<ApiResponse<List<ReviewsDto>>> getFilteredReviews(
            @PathVariable Long hotelId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) ReviewCard reviewCard) {

        List<ReviewsDto> reviews = reviewsService.getFilteredReviews(hotelId, sortBy, reviewCard);
        return ResponseEntity.ok(ApiResponse.success(200, "success", reviews));
    }

    @GetMapping("/hotel/{hotelId}/stats")
    @Operation(summary = "리뷰 카드별 통계")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getReviewStats(
            @PathVariable Long hotelId) {

        Map<String, Long> stats = reviewsService.getReviewCardStats(hotelId);
        return ResponseEntity.ok(ApiResponse.success(200, "success", stats));
    }

    @GetMapping("/hotel/{hotelId}/rating-stats")
    @Operation(summary = "호텔 평점 통계")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getHotelRatingStats(
            @PathVariable Long hotelId) {

        Map<String, Object> stats = reviewsService.getHotelRatingStats(hotelId);
        return ResponseEntity.ok(ApiResponse.success(200, "success", stats));
    }

    @PostMapping
    @Operation(summary = "리뷰 작성")
    public ResponseEntity<ApiResponse<ReviewsDto>> createReview(
            @Valid @RequestBody ReviewsDto reviewsDto,
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        String token = jwtUtil.extractToken(authorization);

        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.getMemberIdFromToken(token);
        reviewsDto.setMemberId(memberId);

        ReviewsDto created = reviewsService.createReview(reviewsDto);

        log.info("리뷰 작성 완료 - memberId: {}, hotelId: {}, reviewId: {}",
                memberId, reviewsDto.getHotelId(), created.getId());

        return ResponseEntity.ok(ApiResponse.success(200, "리뷰가 작성되었습니다.", created));
    }

    @PutMapping("/{reviewId}")
    @Operation(summary = "리뷰 수정")
    public ResponseEntity<ApiResponse<ReviewsDto>> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewsDto reviewsDto,
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        String token = jwtUtil.extractToken(authorization);

        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.getMemberIdFromToken(token);
        ReviewsDto updated = reviewsService.updateReview(reviewId, reviewsDto, memberId);

        log.info("리뷰 수정 완료 - reviewId: {}, memberId: {}", reviewId, memberId);

        return ResponseEntity.ok(ApiResponse.success(200, "리뷰가 수정되었습니다.", updated));
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "리뷰 삭제")
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        String token = jwtUtil.extractToken(authorization);

        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.getMemberIdFromToken(token);
        reviewsService.deleteReview(reviewId, memberId);

        log.info("리뷰 삭제 완료 - reviewId: {}, memberId: {}", reviewId, memberId);

        return ResponseEntity.ok(ApiResponse.success(200, "리뷰가 삭제되었습니다.", null));
    }

    @GetMapping("/eligibility")
    @Operation(summary = "리뷰 작성 가능 여부 체크",
            description = "결제 완료 + 체크아웃 날짜 지남 + 1개만 작성 가능 조건 체크")
    public ResponseEntity<ApiResponse<ReviewEligibilityDto>> checkReviewEligibility(
            @RequestParam Long hotelId,
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        String token = jwtUtil.extractToken(authorization);

        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.getMemberIdFromToken(token);
        ReviewEligibilityDto eligibility = reviewsService.checkReviewEligibility(hotelId, memberId);

        return ResponseEntity.ok(ApiResponse.success(200, "success", eligibility));
    }

    @GetMapping("/my-review")
    @Operation(summary = "내가 작성한 리뷰 조회", description = "특정 호텔에 대한 본인의 리뷰를 조회합니다.")
    public ResponseEntity<ApiResponse<ReviewsDto>> getMyReview(
            @RequestParam Long hotelId,
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        String token = jwtUtil.extractToken(authorization);

        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 토큰입니다.");
        }

        Long memberId = jwtUtil.getMemberIdFromToken(token);
        ReviewsDto myReview = reviewsService.getMyReview(hotelId, memberId);

        if (myReview == null) {
            return ResponseEntity.ok(ApiResponse.success(404, "작성한 리뷰가 없습니다.", null));
        }

        return ResponseEntity.ok(ApiResponse.success(200, "success", myReview));
    }
}