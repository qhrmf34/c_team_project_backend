package com.hotel_project.payment_jpa.coupon.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.payment_jpa.coupon.dto.CouponDto;
import com.hotel_project.payment_jpa.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/coupons")
@Tag(name = "Coupon API", description = "쿠폰 관리 API")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @GetMapping
    @Operation(summary = "쿠폰 검색", description = "쿠폰명으로 검색합니다. 검색어가 없으면 전체 조회")
    public ResponseEntity<ApiResponse<Page<CouponDto>>> findByName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CouponDto> countries = couponService.findByName(pageable, search);
        return ResponseEntity.ok(ApiResponse.success(200, "success", countries));
    }

    // 이제 search endpoint는 제거됨 (findByName으로 통합)

    @GetMapping("/{id}")
    @Operation(summary = "쿠폰 단건 조회", description = "ID로 쿠폰를 조회합니다.")
    public ResponseEntity<ApiResponse<CouponDto>> findById(@PathVariable Long id) throws CommonExceptionTemplate {
        CouponDto couponDto = couponService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", couponDto));
    }

    @PostMapping
    @Operation(summary = "쿠폰 등록", description = "새로운 쿠폰를 등록합니다.")
    public ResponseEntity<ApiResponse<String>> save(@Valid @RequestBody CouponDto couponDto, BindingResult bindingResult) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }
        String result = couponService.insert(couponDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "쿠폰 수정", description = "기존 쿠폰 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> update(@PathVariable Long id, @RequestBody CouponDto couponDto) throws CommonExceptionTemplate {
        couponDto.setId(id);
        String result = couponService.update(couponDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "쿠폰 삭제", description = "쿠폰를 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) throws CommonExceptionTemplate {
        String result = couponService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }
}