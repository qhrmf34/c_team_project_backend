package com.hotel_project.member_jpa.member.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteSignupRequest {

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 100, message = "이메일은 100자 이하로 입력해야 합니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수 입력입니다.")
    @Size(max = 30, message = "전화번호는 30자 이하로 입력해야 합니다.")
    private String phoneNumber;

    // 나중에 주소 추가 예정
    // private String address;
}