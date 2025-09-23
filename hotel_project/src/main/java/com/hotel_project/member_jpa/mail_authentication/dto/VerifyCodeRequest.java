package com.hotel_project.member_jpa.mail_authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCodeRequest {

    @NotBlank(message = "이메일은 필수 입력입니다.")
    private String email;

    @NotBlank(message = "인증 코드는 필수 입력입니다.")
    @Size(min = 6, max = 6, message = "인증 코드는 6자리여야 합니다.")
    private String verificationCode;
}