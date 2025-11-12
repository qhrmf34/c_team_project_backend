package com.hotel_project.member_jpa.member.controller;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.service.TokenBlacklistService;
import com.hotel_project.common_jpa.service.TurnstileService;
import com.hotel_project.common_jpa.util.ApiResponse;
import com.hotel_project.common_jpa.util.JwtUtil;
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
@Tag(name = "Member API", description = "회원 관리API")
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final TurnstileService turnstileService;

    // ================= 회원가입 / 로그인 =================
    @PostMapping("/signup")
    @Operation(summary = "일반 회원가입", description = "이메일/비밀번호로 회원가입합니다.")
    public ResponseEntity<ApiResponse<LoginResponse>> signup(
            @Valid @RequestBody SignupRequest signupRequest,
            BindingResult bindingResult
    ) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors()
                    .forEach(error -> errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        // Turnstile 검증
        turnstileService.verifyToken(signupRequest.getTurnstileToken());

        LoginResponse response = memberService.signup(signupRequest);
        return ResponseEntity.ok(ApiResponse.success(200, "회원가입이 완료되었습니다.", response));
    }

    @PostMapping("/login")
    @Operation(summary = "일반 로그인", description = "이메일/비밀번호로 로그인합니다.")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            BindingResult bindingResult
    ) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors()
                    .forEach(error -> errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        // Turnstile 검증
        turnstileService.verifyToken(loginRequest.getTurnstileToken());

        LoginResponse response = memberService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(200, "로그인이 완료되었습니다.", response));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 토큰을 무효화합니다.")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authorization)
            throws CommonExceptionTemplate {
        String token = jwtUtil.extractToken(authorization);
        String jwtId = jwtUtil.getJwtIdFromToken(token);
        tokenBlacklistService.addToBlacklist(jwtId);

        return ResponseEntity.ok(ApiResponse.success(200, "로그아웃이 완료되었습니다.", null));
    }

    // ================= 비밀번호 재설정 관련 API =================
    @PostMapping("/forgot-password")
    @Operation(summary = "비밀번호 재설정 요청", description = "이메일로 인증 코드를 전송합니다.")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            BindingResult bindingResult
    ) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors()
                    .forEach(error -> errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        String result = emailService.sendPasswordResetCode(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(200, result, null));
    }

    @PostMapping("/verify-reset-code")
    @Operation(summary = "비밀번호 재설정 인증 코드 확인", description = "인증 코드를 확인합니다.")
    public ResponseEntity<ApiResponse<String>> verifyResetCode(
            @Valid @RequestBody VerifyCodeRequest request,
            BindingResult bindingResult
    ) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors()
                    .forEach(error -> errorMessages.append(error.getDefaultMessage()).append(" "));
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
            BindingResult bindingResult
    ) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors()
                    .forEach(error -> errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new CommonExceptionTemplate(400, "비밀번호가 일치하지 않습니다.");
        }

        String result = memberService.resetPasswordDirect(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(200, result, null));
    }

    // ================= 기존 API =================
    @GetMapping("/profile")
    @Operation(summary = "회원 정보 조회", description = "JWT 토큰으로 회원 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<MemberDto>> getProfile(@RequestHeader("Authorization") String authorization)
            throws CommonExceptionTemplate {
        String token = jwtUtil.extractToken(authorization);
        MemberDto profile = memberService.getMemberDtoByToken(token);
        return ResponseEntity.ok(ApiResponse.success(200, "회원 정보 조회 완료", profile));
    }

    @PostMapping("/profile")
    @Operation(summary = "회원 정보 수정", description = "JWT 토큰으로 회원 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<MemberDto>> updateProfile(
            @RequestHeader("Authorization") String authorization,
            @RequestBody MemberDto memberDto
    ) throws CommonExceptionTemplate {
        String token = jwtUtil.extractToken(authorization);
        MemberDto currentMember = memberService.getMemberDtoByToken(token);

        if (currentMember.getProvider() != Provider.local) {
            throw new CommonExceptionTemplate(403,
                    "소셜 로그인 계정은 회원 정보를 수정할 수 없습니다. Provider: " + currentMember.getProvider());
        }

        MemberDto updatedProfile = memberService.updateMemberAndReturnDto(currentMember.getId(), memberDto);
        return ResponseEntity.ok(ApiResponse.success(200, "회원 정보 수정 완료", updatedProfile));
    }

    @DeleteMapping("/profile")
    @Operation(summary = "회원 탈퇴", description = "JWT 토큰으로 회원을 탈퇴시킵니다.")
    public ResponseEntity<ApiResponse<String>> deleteProfile(@RequestHeader("Authorization") String authorization)
            throws CommonExceptionTemplate {
        String token = jwtUtil.extractToken(authorization);
        MemberDto member = memberService.getMemberDtoByToken(token);
        String result = memberService.deleteMember(member.getId());
        return ResponseEntity.ok(ApiResponse.success(200, "회원 탈퇴가 완료되었습니다.", result));
    }

    @PostMapping("/match-password")
    @Operation(summary = "현재 비밀번호 확인", description = "현재 비밀번호가 일치하는지 확인합니다.")
    public ResponseEntity<ApiResponse<String>> matchPassword(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody MatchPasswordRequest request,
            BindingResult bindingResult
    ) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors()
                    .forEach(error -> errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        String token = jwtUtil.extractToken(authorization);
        MemberDto currentMember = memberService.getMemberDtoByToken(token);

        if (currentMember.getProvider() != Provider.local) {
            throw new CommonExceptionTemplate(403, "소셜 로그인 계정은 비밀번호를 변경할 수 없습니다.");
        }

        boolean isMatch = memberService.verifyCurrentPassword(currentMember.getId(), request.getPassword());
        if (!isMatch) {
            throw new CommonExceptionTemplate(400, "현재 비밀번호가 일치하지 않습니다.");
        }

        return ResponseEntity.ok(ApiResponse.success(200, "비밀번호가 확인되었습니다.", null));
    }

    @PostMapping("/account-reset-password")
    @Operation(summary = "계정 비밀번호 변경", description = "로그인된 사용자의 비밀번호를 변경합니다.")
    public ResponseEntity<ApiResponse<String>> changeAccountPassword(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody ChangePasswordRequest request,
            BindingResult bindingResult
    ) throws CommonExceptionTemplate {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            bindingResult.getFieldErrors()
                    .forEach(error -> errorMessages.append(error.getDefaultMessage()).append(" "));
            throw new CommonExceptionTemplate(400, errorMessages.toString().trim());
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new CommonExceptionTemplate(400, "새 비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.extractToken(authorization);
        MemberDto currentMember = memberService.getMemberDtoByToken(token);

        if (currentMember.getProvider() != Provider.local) {
            throw new CommonExceptionTemplate(403, "소셜 로그인 계정은 비밀번호를 변경할 수 없습니다.");
        }

        String result = memberService.changePassword(currentMember.getId(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(200, result, null));
    }

    //소셜 로그인 추가 정보 입력 - JWT 기반으로 수정
    @PostMapping("/complete-social-signup")
    public ResponseEntity<LoginResponse> completeSocialSignup(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CompleteSignupRequest request
    ) throws CommonExceptionTemplate {

        System.out.println("=== 소셜 회원가입 완료 API 호출 ===");

        // Bearer 토큰 추출
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CommonExceptionTemplate(401, "유효하지 않은 인증 헤더입니다.");
        }

        String tempToken = authHeader.substring(7);
        System.out.println("임시 토큰 수신 완료");

        // 서비스 호출
        LoginResponse response = memberService.completeSocialSignup(tempToken, request);

        System.out.println("✓ 소셜 회원가입 완료 - 회원 ID: " + response.getMemberId());

        return ResponseEntity.ok(response);
    }
}