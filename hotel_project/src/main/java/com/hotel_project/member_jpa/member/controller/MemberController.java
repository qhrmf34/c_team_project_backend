package com.hotel_project.member_jpa.member.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.member_jpa.mail_authentication.dto.ForgotPasswordRequest;
import com.hotel_project.member_jpa.mail_authentication.dto.ResetPasswordRequest;
import com.hotel_project.member_jpa.mail_authentication.dto.VerifyCodeRequest;
import com.hotel_project.member_jpa.member.dto.*;
import com.hotel_project.member_jpa.mail_authentication.service.EmailService;
import com.hotel_project.member_jpa.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Tag(name = "Member API", description = "회원 관리 API")
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;

    @PostMapping("/signup")
    @Operation(summary = "일반 회원가입", description = "이메일/비밀번호로 회원가입합니다.")
    public ResponseEntity<ApiResponse<LoginResponse>> signup(
            @Valid @RequestBody SignupRequest signupRequest,
            BindingResult bindingResult) throws CommonExceptionTemplate {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" ")
            );
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        LoginResponse response = memberService.signup(signupRequest);
        return ResponseEntity.ok(ApiResponse.success(200, "회원가입이 완료되었습니다.", response));
    }

    @PostMapping("/login")
    @Operation(summary = "일반 로그인", description = "이메일/비밀번호로 로그인합니다.")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            BindingResult bindingResult) throws CommonExceptionTemplate {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" ")
            );
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        LoginResponse response = memberService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(200, "로그인이 완료되었습니다.", response));
    }

    // ========== 비밀번호 재설정 관련 API (MyBatis 방식) ==========

    @PostMapping("/forgot-password")
    @Operation(summary = "비밀번호 재설정 요청", description = "이메일로 인증 코드를 전송합니다.")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            BindingResult bindingResult) throws CommonExceptionTemplate {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" ")
            );
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        String result = emailService.sendPasswordResetCode(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(200, result, null));
    }

    @PostMapping("/verify-reset-code")
    @Operation(summary = "비밀번호 재설정 인증 코드 확인", description = "인증 코드를 확인합니다.")
    public ResponseEntity<ApiResponse<String>> verifyResetCode(
            @Valid @RequestBody VerifyCodeRequest request,
            BindingResult bindingResult) throws CommonExceptionTemplate {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" ")
            );
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        boolean isValid = emailService.verifyCode(request.getEmail(), request.getVerificationCode());

        if (!isValid) {
            throw new CommonExceptionTemplate(400, "유효하지 않거나 만료된 인증 코드입니다.");
        }

        return ResponseEntity.ok(ApiResponse.success(200, "인증 코드가 확인되었습니다.", null));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "비밀번호 재설정", description = "새로운 비밀번호로 재설정합니다.")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            BindingResult bindingResult) throws CommonExceptionTemplate {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors().forEach(error ->
                    errorMessages.append(error.getDefaultMessage()).append(" ")
            );
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        // 비밀번호 확인
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new CommonExceptionTemplate(400, "비밀번호가 일치하지 않습니다.");
        }

        // 인증 코드 재확인 제거 - 이미 verify-reset-code에서 검증 완료
        String result = memberService.resetPasswordDirect(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(200, result, null));
    }

    // ========== 기존 API들 ==========

    @GetMapping("/profile")
    @Operation(summary = "회원 정보 조회", description = "JWT 토큰으로 회원 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MemberDto>> getProfile(
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        String token = extractToken(authorization);
        MemberDto profile = memberService.getMemberDtoByToken(token);

        return ResponseEntity.ok(ApiResponse.success(200, "회원 정보 조회 완료", profile));
    }

    @PutMapping("/profile")
    @Operation(summary = "회원 정보 수정", description = "JWT 토큰으로 회원 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<MemberDto>> updateProfile(
            @RequestHeader("Authorization") String authorization,
            @RequestBody MemberDto memberDto) throws CommonExceptionTemplate {

        String token = extractToken(authorization);
        MemberDto currentMember = memberService.getMemberDtoByToken(token);

        MemberDto updatedProfile = memberService.updateMemberAndReturnDto(currentMember.getId(), memberDto);

        return ResponseEntity.ok(ApiResponse.success(200, "회원 정보 수정 완료", updatedProfile));
    }

    @DeleteMapping("/profile")
    @Operation(summary = "회원 탈퇴", description = "JWT 토큰으로 회원을 탈퇴시킵니다.")
    public ResponseEntity<ApiResponse<String>> deleteProfile(
            @RequestHeader("Authorization") String authorization) throws CommonExceptionTemplate {

        String token = extractToken(authorization);
        MemberDto member = memberService.getMemberDtoByToken(token);

        String result = memberService.deleteMember(member.getId());
        return ResponseEntity.ok(ApiResponse.success(200, "회원 탈퇴가 완료되었습니다.", result));
    }

    /**
     * Authorization 헤더에서 토큰 추출
     */
    private String extractToken(String authorization) throws CommonExceptionTemplate {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new CommonExceptionTemplate(401, "Bearer 토큰이 필요합니다.");
        }
        return authorization.substring(7);
    }
}