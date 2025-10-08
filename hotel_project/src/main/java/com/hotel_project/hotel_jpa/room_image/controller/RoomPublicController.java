package com.hotel_project.hotel_jpa.room_image.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.hotel_jpa.room_image.dto.RoomImageDto;
import com.hotel_project.hotel_jpa.room_image.service.RoomImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/rooms")
@Tag(name = "Room Public API", description = "객실 공개 API")
public class RoomPublicController {

    @Autowired
    private RoomImageService roomImageService;

    @GetMapping("/{roomId}/images")
    @Operation(summary = "객실 이미지 조회", description = "특정 객실의 이미지 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<List<RoomImageDto>>> getRoomImages(@PathVariable Long roomId) throws CommonExceptionTemplate {
        List<RoomImageDto> images = roomImageService.findByRoomId(roomId);
        return ResponseEntity.ok(ApiResponse.success(200, "success", images));
    }
}