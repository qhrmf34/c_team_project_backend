package com.hotel_project.hotel_jpa.room_pricing.controller;

import com.hotel_project.common_jpa.dto.PublicSearchDto;
import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.hotel_jpa.hotel.dto.HotelDto;
import com.hotel_project.hotel_jpa.hotel.dto.HotelViewDto;
import com.hotel_project.hotel_jpa.hotel.service.HotelService;
import com.hotel_project.hotel_jpa.room_pricing.dto.RoomPricingDto;
import com.hotel_project.hotel_jpa.room_pricing.dto.RoomPricingViewDto;
import com.hotel_project.hotel_jpa.room_pricing.service.RoomPricingService;
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
@RequestMapping("/api/admin/room_pricing")
@Tag(name = "Room Pricing API", description = "객실 가격 관리 API")
public class RoomPricingController {

    @Autowired
    private RoomPricingService roomPricingService;

    @GetMapping
    @Operation(summary = "객실 가격 검색", description = "객실명으로 검색합니다. 검색어가 없으면 전체 조회")
    public ResponseEntity<ApiResponse<Page<RoomPricingViewDto>>> findByName(
    PublicSearchDto publicSearchDto) {

        Pageable pageable = PageRequest.of(publicSearchDto.getPage(), publicSearchDto.getSize());
        Page<RoomPricingViewDto> roomPricings = roomPricingService.findByName(pageable, publicSearchDto.getSearch());
        return ResponseEntity.ok(ApiResponse.success(200, "success", roomPricings));
    }

    @GetMapping("/{id}")
    @Operation(summary = "객실 가격 단건 조회", description = "ID로 객실 가격을 조회합니다.")
    public ResponseEntity<ApiResponse<RoomPricingDto>> findById(@PathVariable Long id) throws CommonExceptionTemplate {
        RoomPricingDto roomPricingDto = roomPricingService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", roomPricingDto));
    }

    @PostMapping
    @Operation(summary = "객실 가격 등록", description = "새로운 객실 가격을 등록합니다.")
    public ResponseEntity<ApiResponse<String>> save(
            @Valid @RequestBody RoomPricingDto roomPricingDto,
            BindingResult bindingResult) throws CommonExceptionTemplate {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        String result = roomPricingService.insert(roomPricingDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "객실 가격 수정", description = "기존 객실 가격 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> update(
            @PathVariable Long id,
            @RequestBody RoomPricingDto roomPricingDto) throws CommonExceptionTemplate {

        roomPricingDto.setId(id);
        String result = roomPricingService.update(roomPricingDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "객실 가격 삭제", description = "객실 가격을 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) throws CommonExceptionTemplate {
        String result = roomPricingService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }
}