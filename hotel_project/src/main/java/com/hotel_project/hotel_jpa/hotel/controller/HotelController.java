package com.hotel_project.hotel_jpa.hotel.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.hotel_jpa.hotel.dto.HotelDto;
import com.hotel_project.hotel_jpa.hotel.dto.HotelViewDto;
import com.hotel_project.hotel_jpa.hotel.service.HotelService;
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
@RequestMapping("/api/admin/hotels")
@Tag(name = "Hotels API", description = "호텔 관리 API")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @GetMapping
    @Operation(summary = "도시 검색", description = "도시명으로 검색합니다. 검색어가 없으면 전체 조회")
    public ResponseEntity<ApiResponse<Page<HotelViewDto>>> findByName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<HotelViewDto> cities = hotelService.findByName(pageable, search);
        return ResponseEntity.ok(ApiResponse.success(200, "success", cities));
    }

    @GetMapping("/{id}")
    @Operation(summary = "도시 단건 조회", description = "ID로 도시를 조회합니다.")
    public ResponseEntity<ApiResponse<HotelDto>> findById(@PathVariable Long id) throws CommonExceptionTemplate {
        HotelDto hotelDto = hotelService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", hotelDto));
    }

    @PostMapping
    @Operation(summary = "도시 등록", description = "새로운 도시를 등록합니다.")
    public ResponseEntity<ApiResponse<String>> save(@Valid @RequestBody HotelDto hotelDto, BindingResult bindingResult) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }
        String result = hotelService.insert(hotelDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "도시 수정", description = "기존 도시 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> update(@PathVariable Long id, @RequestBody HotelDto hotelDto) throws CommonExceptionTemplate {
        hotelDto.setId(id);
        String result = hotelService.update(hotelDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "도시 삭제", description = "도시를 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) throws CommonExceptionTemplate {
        String result = hotelService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }
}