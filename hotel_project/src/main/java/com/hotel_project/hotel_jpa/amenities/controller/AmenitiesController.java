package com.hotel_project.hotel_jpa.amenities.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesDto;
import com.hotel_project.hotel_jpa.amenities.service.AmenitiesService;
import com.hotel_project.hotel_jpa.country.dto.CountryDto;
import com.hotel_project.hotel_jpa.country.service.CountryService;
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
@RequestMapping("/api/admin/amenities")
@Tag(name = "Amenities API", description = "편의 시설 관리 API")
public class AmenitiesController {

    @Autowired
    private AmenitiesService amenitiesService;

    @GetMapping
    @Operation(summary = "국가 검색", description = "국가명으로 검색합니다. 검색어가 없으면 전체 조회")
    public ResponseEntity<ApiResponse<Page<AmenitiesDto>>> findByName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AmenitiesDto> countries = amenitiesService.findByName(pageable, search);
        return ResponseEntity.ok(ApiResponse.success(200, "success", countries));
    }

    // 이제 search endpoint는 제거됨 (findByName으로 통합)

    @GetMapping("/{id}")
    @Operation(summary = "국가 단건 조회", description = "ID로 국가를 조회합니다.")
    public ResponseEntity<ApiResponse<AmenitiesDto>> findById(@PathVariable Long id) throws CommonExceptionTemplate {
        AmenitiesDto amenitiesDto = amenitiesService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", amenitiesDto));
    }

    @PostMapping
    @Operation(summary = "국가 등록", description = "새로운 국가를 등록합니다.")
    public ResponseEntity<ApiResponse<String>> save(@Valid @RequestBody AmenitiesDto amenitiesDto, BindingResult bindingResult) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }
        String result = amenitiesService.insert(amenitiesDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "국가 수정", description = "기존 국가 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> update(@PathVariable Long id, @RequestBody AmenitiesDto amenitiesDto) throws CommonExceptionTemplate {
        amenitiesDto.setId(id);
        String result = amenitiesService.update(amenitiesDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "국가 삭제", description = "국가를 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) throws CommonExceptionTemplate {
        String result = amenitiesService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }
}