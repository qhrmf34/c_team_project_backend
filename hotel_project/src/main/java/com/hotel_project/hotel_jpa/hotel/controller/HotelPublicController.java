package com.hotel_project.hotel_jpa.hotel.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.hotel_jpa.hotel.dto.*;
import com.hotel_project.hotel_jpa.hotel.service.HotelPublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@Tag(name = "Public Hotels API", description = "사용자용 호텔 검색 API")
public class HotelPublicController {

    @Autowired
    private HotelPublicService hotelPublicService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping
    @Operation(summary = "호텔 검색 및 필터링")
    public ResponseEntity<ApiResponse<HotelSearchResponseDto>> searchHotels(
            HotelSearchRequestDto searchRequest,

            @Parameter(description = "시작 위치 (0부터 시작)")
            @RequestParam(defaultValue = "0") Long offset,

            @Parameter(description = "가져올 개수")
            @RequestParam(defaultValue = "3") Integer size,

            @RequestHeader(value = "Authorization", required = false) String authorization) {

        // JWT에서 memberId 추출
        Long memberId = null;
        if (authorization != null) {
            try {
                String token = jwtUtil.extractToken(authorization);
                if (jwtUtil.validateToken(token)) {
                    memberId = jwtUtil.getMemberIdFromToken(token);
                }
            } catch (Exception e) {
                // 토큰 오류 시 무시하고 비로그인 상태로 처리
            }
        }

        searchRequest.setOffset(offset);
        searchRequest.setSize(size);

        HotelSearchResponseDto result = hotelPublicService.searchHotels(searchRequest, memberId);

        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "호텔 상세 조회")
    public ResponseEntity<ApiResponse<HotelDetailDto>> getHotelDetail(
            @Parameter(description = "호텔 ID")
            @PathVariable Long id,

            @Parameter(description = "체크인")
            @RequestParam(required = false) LocalDate checkIn,

            @Parameter(description = "체크아웃")
            @RequestParam(required = false) LocalDate checkOut,

            @RequestHeader(value = "Authorization", required = false) String authorization)
            throws CommonExceptionTemplate {

        // JWT에서 memberId 추출
        Long memberId = null;
        if (authorization != null) {
            try {
                String token = jwtUtil.extractToken(authorization);
                if (jwtUtil.validateToken(token)) {
                    memberId = jwtUtil.getMemberIdFromToken(token);
                }
            } catch (Exception e) {
                // 토큰 오류 시 무시하고 비로그인 상태로 처리
            }
        }

        HotelDetailDto hotelDetail = hotelPublicService.getHotelDetail(id, checkIn, checkOut, memberId);
        return ResponseEntity.ok(ApiResponse.success(200, "success", hotelDetail));
    }

    @GetMapping("/filters")
    @Operation(summary = "필터 옵션 조회")
    public ResponseEntity<ApiResponse<FilterOptionsDto>> getFilterOptions() {
        FilterOptionsDto filters = hotelPublicService.getFilterOptions();
        return ResponseEntity.ok(ApiResponse.success(200, "success", filters));
    }

    @GetMapping("/wishlist")
    @Operation(summary = "찜한 호텔 목록 조회")
    public ResponseEntity<ApiResponse<List<HotelSummaryDto>>> getWishlistHotels(
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        // JWT에서 memberId 추출
        String token = jwtUtil.extractToken(authorization);
        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "로그인이 필요합니다.");
        }
        Long memberId = jwtUtil.getMemberIdFromToken(token);

        if (memberId == null) {
            throw MemberException.INVALID_ID.getException();
        }

        List<HotelSummaryDto> wishlistHotels = hotelPublicService.getWishlistHotels(memberId);

        return ResponseEntity.ok(ApiResponse.success(200, "success", wishlistHotels));
    }
}