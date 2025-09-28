package com.hotel_project.hotel_jpa.hotel_amenities.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.hotel_jpa.hotel_amenities.dto.HotelAmenitiesDto;
import com.hotel_project.hotel_jpa.hotel_amenities.dto.HotelAmenitiesViewDto;
import com.hotel_project.hotel_jpa.hotel_amenities.service.HotelAmenitiesService;
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
@RequestMapping("/api/admin/hotel_amenities")
@Tag(name = "Hotel Amenities API", description = "호텔 편의시설 관리 API")
public class HotelAmenitiesController {

    @Autowired
    private HotelAmenitiesService hotelAmenitiesService;

    @GetMapping
    @Operation(summary = "호텔 편의시설 검색", description = "호텔명 또는 편의시설명으로 검색합니다. 검색어가 없으면 전체 조회")
    public ResponseEntity<ApiResponse<Page<HotelAmenitiesViewDto>>> findByName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<HotelAmenitiesViewDto> hotelAmenities = hotelAmenitiesService.findByName(pageable, search);
        return ResponseEntity.ok(ApiResponse.success(200, "success", hotelAmenities));
    }

    @GetMapping("/{id}")
    @Operation(summary = "호텔 편의시설 단건 조회", description = "ID로 호텔 편의시설을 조회합니다.")
    public ResponseEntity<ApiResponse<HotelAmenitiesDto>> findById(@PathVariable Long id) throws CommonExceptionTemplate {
        HotelAmenitiesDto hotelAmenitiesDto = hotelAmenitiesService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", hotelAmenitiesDto));
    }

    @PostMapping
    @Operation(summary = "호텔 편의시설 등록", description = "새로운 호텔 편의시설을 등록합니다.")
    public ResponseEntity<ApiResponse<String>> save(@Valid @RequestBody HotelAmenitiesDto hotelAmenitiesDto, BindingResult bindingResult) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }
        String result = hotelAmenitiesService.insert(hotelAmenitiesDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "호텔 편의시설 수정", description = "기존 호텔 편의시설 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> update(@PathVariable Long id, @RequestBody HotelAmenitiesDto hotelAmenitiesDto) throws CommonExceptionTemplate {
        hotelAmenitiesDto.setId(id);
        String result = hotelAmenitiesService.update(hotelAmenitiesDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "호텔 편의시설 삭제", description = "호텔 편의시설을 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) throws CommonExceptionTemplate {
        String result = hotelAmenitiesService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }
}