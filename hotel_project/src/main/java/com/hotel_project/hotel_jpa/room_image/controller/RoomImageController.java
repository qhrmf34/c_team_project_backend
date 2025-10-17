package com.hotel_project.hotel_jpa.room_image.controller;

import com.hotel_project.common_jpa.dto.PublicSearchDto;
import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.hotel_jpa.room_image.dto.RoomImageDto;
import com.hotel_project.hotel_jpa.room_image.dto.RoomImageViewDto;
import com.hotel_project.hotel_jpa.room_image.service.RoomImageService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/room_images")
@Tag(name = "Room Images API", description = "객실 이미지 관리 API")
public class RoomImageController {

    @Autowired
    private RoomImageService roomImageService;

    @GetMapping
    @Operation(summary = "객실 이미지 검색", description = "객실 이미지명으로 검색합니다. 검색어가 없으면 전체 조회")
    public ResponseEntity<ApiResponse<Page<RoomImageViewDto>>> findByName(
    PublicSearchDto publicSearchDto) {

        Pageable pageable = PageRequest.of(publicSearchDto.getPage(), publicSearchDto.getSize());
        Page<RoomImageViewDto> roomImages = roomImageService.findByName(pageable, publicSearchDto.getSearch());
        return ResponseEntity.ok(ApiResponse.success(200, "success", roomImages));
    }

    @GetMapping("/{id}")
    @Operation(summary = "객실 이미지 단건 조회", description = "ID로 객실 이미지를 조회합니다.")
    public ResponseEntity<ApiResponse<RoomImageDto>> findById(@PathVariable Long id) throws CommonExceptionTemplate {
        RoomImageDto roomImageDto = roomImageService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", roomImageDto));
    }

    @PostMapping
    @Operation(summary = "객실 이미지 등록", description = "새로운 객실 이미지를 등록합니다.")
    public ResponseEntity<ApiResponse<String>> save(@Valid @RequestBody RoomImageDto roomImageDto, BindingResult bindingResult) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }
        String result = roomImageService.insert(roomImageDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "객실 이미지 수정", description = "기존 객실 이미지 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<String>> update(@PathVariable Long id, @RequestBody RoomImageDto roomImageDto) throws CommonExceptionTemplate {
        roomImageDto.setId(id);
        String result = roomImageService.update(roomImageDto);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "객실 이미지 삭제", description = "객실 이미지를 삭제합니다.")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) throws CommonExceptionTemplate {
        String result = roomImageService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }


}