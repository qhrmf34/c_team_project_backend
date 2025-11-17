package com.hotel_project.member_jpa.member_image.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.member_jpa.member_image.dto.ImageType;
import com.hotel_project.member_jpa.member_image.dto.MemberImageDto;
import com.hotel_project.member_jpa.member_image.service.MemberImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/member/images")
@Tag(name = "Member Images API", description = "회원 이미지 관리 API")
@RequiredArgsConstructor
public class MemberImageController {

    private final MemberImageService memberImageService;
    private final JwtUtil jwtUtil;

    @GetMapping("/profile")
    @Operation(summary = "프로필 이미지 조회")
    public ResponseEntity<ApiResponse<Map<String, String>>> getProfileImage(
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        Long memberId = extractMemberId(authorization);
        String imagePath = memberImageService.getMemberImage(memberId, ImageType.profile);

        Map<String, String> result = new HashMap<>();
        result.put("imagePath", imagePath);

        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }
    // ✅ 특정 회원의 프로필 이미지 조회 (관리자용)
    @GetMapping("/profile/member/{memberId}")
    public ResponseEntity<ApiResponse<MemberImageDto>> getProfileImageByMemberId(
            @PathVariable Long memberId
    ) {
        try {
            MemberImageDto image = memberImageService.getProfileImageByMemberId(memberId);
            return ResponseEntity.ok(ApiResponse.success(200, "프로필 이미지 조회 완료", image));
        } catch (Exception e) {
            // 이미지 없으면 null 반환
            return ResponseEntity.ok(ApiResponse.success(200, "프로필 이미지 없음", null));
        }
    }
    @GetMapping("/background")
    @Operation(summary = "배경 이미지 조회")
    public ResponseEntity<ApiResponse<Map<String, String>>> getBackgroundImage(
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        Long memberId = extractMemberId(authorization);
        String imagePath = memberImageService.getMemberImage(memberId, ImageType.background);

        Map<String, String> result = new HashMap<>();
        result.put("imagePath", imagePath);

        return ResponseEntity.ok(ApiResponse.success(200, "success", result));
    }

    @PostMapping("/profile")
    @Operation(summary = "프로필 이미지 업로드")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadProfileImage(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("file") MultipartFile file) throws CommonExceptionTemplate {

        Long memberId = extractMemberId(authorization);
        String filePath = memberImageService.uploadMemberImage(memberId, file, ImageType.profile);

        Map<String, String> result = new HashMap<>();
        result.put("imagePath", filePath);

        return ResponseEntity.ok(ApiResponse.success(200, "프로필 이미지가 업로드되었습니다.", result));
    }

    @PostMapping("/background")
    @Operation(summary = "배경 이미지 업로드")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadBackgroundImage(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("file") MultipartFile file) throws CommonExceptionTemplate {

        Long memberId = extractMemberId(authorization);
        String filePath = memberImageService.uploadMemberImage(memberId, file, ImageType.background);

        Map<String, String> result = new HashMap<>();
        result.put("imagePath", filePath);

        return ResponseEntity.ok(ApiResponse.success(200, "배경 이미지가 업로드되었습니다.", result));
    }

    // JWT에서 memberId 추출
    private Long extractMemberId(String authorization) throws CommonExceptionTemplate {
        String token = jwtUtil.extractToken(authorization);
        if (!jwtUtil.validateToken(token)) {
            throw new CommonExceptionTemplate(401, "로그인이 필요합니다.");
        }
        Long memberId = jwtUtil.getMemberIdFromToken(token);
        if (memberId == null) {
            throw MemberException.INVALID_ID.getException();
        }
        return memberId;
    }
}