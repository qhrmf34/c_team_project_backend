package com.hotel_project.hotel_jpa.hotel.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.hotel_jpa.hotel.dto.*;
import com.hotel_project.hotel_jpa.hotel.service.HotelPublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
            @Parameter(description = "목적지")
            @RequestParam(required = false) String destination,

            @Parameter(description = "체크인")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,

            @Parameter(description = "체크아웃")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,

            @Parameter(description = "투숙객 수")
            @RequestParam(required = false) Integer guests,

            @Parameter(description = "객실 수")
            @RequestParam(required = false, defaultValue = "1") Integer rooms,

            @Parameter(description = "최소 가격")
            @RequestParam(required = false) Integer minPrice,

            @Parameter(description = "최대 가격")
            @RequestParam(required = false) Integer maxPrice,

            @Parameter(description = "최소 평점")
            @RequestParam(required = false) Integer rating,

            @Parameter(description = "호텔 타입 (hotel/motel/resort)")
            @RequestParam(required = false) String hotelType,

            @Parameter(description = "무료 서비스 ID 목록")
            @RequestParam(required = false) List<Long> freebies,

            @Parameter(description = "편의시설 ID 목록")
            @RequestParam(required = false) List<Long> amenities,

            @Parameter(description = "정렬 기준")
            @RequestParam(defaultValue = "recommended") String sortBy,

            @Parameter(description = "페이지 번호")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "10") int size,

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

        HotelSearchRequestDto searchRequest = HotelSearchRequestDto.builder()
                .destination(destination)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .guests(guests)
                .rooms(rooms)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .rating(rating)
                .hotelType(hotelType)
                .freebies(freebies)
                .amenities(amenities)
                .sortBy(sortBy)
                .build();

        Pageable pageable = PageRequest.of(page, size);
        HotelSearchResponseDto result = hotelPublicService.searchHotels(searchRequest, pageable, memberId);

        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "호텔 상세 조회")
    public ResponseEntity<ApiResponse<HotelDetailDto>> getHotelDetail(
            @Parameter(description = "호텔 ID")
            @PathVariable Long id,

            @Parameter(description = "체크인")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,

            @Parameter(description = "체크아웃")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,

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
}