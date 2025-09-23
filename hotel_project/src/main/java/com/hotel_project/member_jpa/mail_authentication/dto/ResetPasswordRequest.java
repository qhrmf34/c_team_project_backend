package com.hotel_project.member_jpa.mail_authentication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "이메일은 필수 입력입니다.")
    private String email;

    @NotBlank(message = "인증 코드는 필수 입력입니다.")
    private String verificationCode;

    @NotBlank(message = "새 비밀번호는 필수 입력입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    @Size(max = 255, message = "비밀번호는 255자 이하로 입력해야 합니다.")
    private String newPassword;

    @NotBlank(message = "비밀번호 확인은 필수 입력입니다.")
    private String confirmPassword;
}
