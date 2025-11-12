package com.hotel_project.member_jpa.member.dto;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "전화번호는 필수 입력입니다.")
    @Size(max = 30, message = "전화번호는30자 이하로 입력해야 합니다.")
    private String phoneNumber;

    @NotBlank(message = "주소는 필수 입력입니다.")
    @Size(max = 50, message = "주소는50자 이하로 입력해야 합니다.")
    private String roadAddress;

    @Size(max = 50, message = "상세 주소는50자 이하로 입력해야 합니다.")
    private String detailAddress;

    @NotBlank(message = "Turnstile 토큰은 필수입니다.")
    private String turnstileToken;
}

