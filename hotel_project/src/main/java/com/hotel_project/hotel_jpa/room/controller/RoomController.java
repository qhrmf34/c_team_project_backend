package com.hotel_project.hotel_jpa.room.controller;

import com.hotel_project.common_jpa.dto.PublicSearchDto;
import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.hotel_jpa.room.dto.*;
import com.hotel_project.hotel_jpa.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/rooms")
@Tag(name = "Rooms API", description = "객실 관리 API")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping
    @Operation(summary = "객실 검색", description = "객실명으로 검색합니다. 검색어가 없으면 전체 조회")
    public ResponseEntity<ApiResponse<Page<RoomViewDto>>> findByName(
            PublicSearchDto publicSearchDto) {

        Pageable pageable = PageRequest.of(publicSearchDto.getPage(), publicSearchDto.getSize());
        Page<RoomViewDto> rooms = roomService.findByName(pageable, publicSearchDto.getSearch());
        return ResponseEntity.ok(ApiResponse.success(200, "success", rooms));
    }

    @GetMapping("/{id}")
    @Operation(summary = "객실 단건 조회", description = "ID로 객실을 조회합니다.")
    public ResponseEntity<ApiResponse<RoomDto>> findById(@PathVariable Long id) throws CommonExceptionTemplate {
        RoomDto roomDto = roomService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", roomDto));
    }

    @PostMapping
    @Operation(summary = "객실 등록", description = "새로운 객실을 등록합니다.")
    public ResponseEntity<ApiResponse<String>> save(@Valid @RequestBody RoomDto roomDto, BindingResult bindingResult) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }
        String result = roomService.insert(roomDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "객실 수정", description = "기존 객실 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> update(@PathVariable Long id, @RequestBody RoomDto roomDto) throws CommonExceptionTemplate {
        roomDto.setId(id);
        String result = roomService.update(roomDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "객실 삭제", description = "객실을 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) throws CommonExceptionTemplate {
        String result = roomService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @GetMapping("/availability")
    @Operation(summary = "날짜별 객실 재고 조회", description = "특정 호텔의 예약 가능한 객실 목록과 가격을 조회합니다.")
    public ResponseEntity<ApiResponse<List<RoomAvailabilityDto>>> getRoomAvailability(
            @RequestParam Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        RoomSearchDto searchDto = RoomSearchDto.builder()
                .hotelId(hotelId)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .build();

        List<RoomAvailabilityDto> rooms = roomService.getRoomAvailability(searchDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", rooms));
    }

    @GetMapping("/{roomId}/daily-prices")
    @Operation(summary = "객실 날짜별 가격 조회", description = "특정 객실의 날짜별 상세 가격을 조회합니다.")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getRoomDailyPrices(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        List<Map<String, Object>> prices = roomService.getRoomDailyPrices(roomId, checkIn, checkOut);
        return ResponseEntity.ok(ApiResponse.success(200, "success", prices));
    }

    @GetMapping("/{roomId}/detail")
    @Operation(summary = "객실 상세 정보 조회", description = "Book Now용 객실 전체 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRoomDetail(
            @PathVariable Long roomId) {

        Map<String, Object> detail = roomService.getRoomDetailById(roomId);
        return ResponseEntity.ok(ApiResponse.success(200, "success", detail));
    }
}