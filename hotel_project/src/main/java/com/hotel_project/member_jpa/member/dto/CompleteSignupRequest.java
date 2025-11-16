package com.hotel_project.member_jpa.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompleteSignupRequest {

    // ✅ 이메일 추가 (카카오, 네이버용 - 필수는 아님, Google은 토큰에서 가져옴)
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 100자 이하로 입력해야 합니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수 입력입니다.")
    @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "올바른 휴대폰 번호 형식이 아닙니다. (하이픈 제외)")
    @Size(max = 30, message = "전화번호는 30자 이하로 입력해야 합니다.")
    private String phoneNumber;

    @NotBlank(message = "주소는 필수 입력입니다.")
    @Size(max = 50, message = "주소는 50자 이하로 입력해야 합니다.")
    private String roadAddress;

    @Size(max = 50, message = "상세 주소는 50자 이하로 입력해야 합니다.")
    private String detailAddress;

    @NotBlank(message = "Turnstile 토큰은 필수입니다.")
    private String turnstileToken;
}