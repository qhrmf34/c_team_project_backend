package com.hotel_project.hotel_jpa.hotel_freebies.controller;

import com.hotel_project.common_jpa.dto.PublicSearchDto;
import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.hotel_jpa.hotel_freebies.dto.HotelFreebiesDto;
import com.hotel_project.hotel_jpa.hotel_freebies.dto.HotelFreebiesViewDto;
import com.hotel_project.hotel_jpa.hotel_freebies.service.HotelFreebiesService;
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
@RequestMapping("/api/admin/hotel_freebies")
@Tag(name = "Hotel Freebies API", description = "호텔 무료시설 관리 API")
public class HotelFreebiesController {

    @Autowired
    private HotelFreebiesService hotelFreebiesService;

    @GetMapping
    @Operation(summary = "호텔 무료시설 검색", description = "호텔명 또는 무료시설명으로 검색합니다. 검색어가 없으면 전체 조회")
    public ResponseEntity<ApiResponse<Page<HotelFreebiesViewDto>>> findByName(
    PublicSearchDto publicSearchDto) {

        Pageable pageable = PageRequest.of(publicSearchDto.getPage(), publicSearchDto.getSize());
        Page<HotelFreebiesViewDto> hotelFreebies = hotelFreebiesService.findByName(pageable, publicSearchDto.getSearch());
        return ResponseEntity.ok(ApiResponse.success(200, "success", hotelFreebies));
    }

    @GetMapping("/{id}")
    @Operation(summary = "호텔 무료시설 단건 조회", description = "ID로 호텔 무료시설을 조회합니다.")
    public ResponseEntity<ApiResponse<HotelFreebiesDto>> findById(@PathVariable Long id) throws CommonExceptionTemplate {
        HotelFreebiesDto hotelFreebiesDto = hotelFreebiesService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", hotelFreebiesDto));
    }

    @PostMapping
    @Operation(summary = "호텔 무료시설 등록", description = "새로운 호텔 무료시설을 등록합니다.")
    public ResponseEntity<ApiResponse<String>> save(@Valid @RequestBody HotelFreebiesDto hotelFreebiesDto, BindingResult bindingResult) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }
        String result = hotelFreebiesService.insert(hotelFreebiesDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "호텔 무료시설 수정", description = "기존 호텔 무료시설 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> update(@PathVariable Long id, @RequestBody HotelFreebiesDto hotelFreebiesDto) throws CommonExceptionTemplate {
        hotelFreebiesDto.setId(id);
        String result = hotelFreebiesService.update(hotelFreebiesDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "호텔 무료시설 삭제", description = "호텔 무료시설을 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) throws CommonExceptionTemplate {
        String result = hotelFreebiesService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }
}