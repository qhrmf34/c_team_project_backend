package com.hotel_project.member_jpa.member.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {
    @NotBlank(message = "성을 입력해주세요.")
    @Size(max = 50, message = "성은 50자 이하로 입력해야 합니다.")
    private String firstName;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 50, message = "이름은 50자 이하로 입력해야 합니다.")
    private String lastName;

    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 100, message = "이메일은 100자 이하로 입력해야 합니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수 입력입니다.")
    @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "올바른 휴대폰 번호 형식이 아닙니다. (하이픈 제외)")
    @Size(max = 30, message = "전화번호는 30자 이하로 입력해야 합니다.")
    private String phoneNumber;

    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Size(min = 8, max = 255, message = "비밀번호는 8자 이상 255자 이하로 입력해야 합니다.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력입니다.")
    private String confirmPassword;

    @AssertTrue(message = "이용약관에 동의해주세요.")
    private boolean agreement;

    @NotBlank(message = "도로명 주소는 필수 입력입니다.")
    @Size(max = 50, message = "도로명 주소는 50자 이하로 입력해야 합니다.")
    private String roadAddress;

    @Size(max = 50, message = "상세 주소는 50자 이하로 입력해야 합니다.")
    private String detailAddress;

    @NotBlank(message = "Turnstile 토큰이 필요합니다.")
    private String turnstileToken;
}