package com.hotel_project.member_jpa.mail_authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {

    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;
}