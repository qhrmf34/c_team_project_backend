package com.hotel_project.member_jpa.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long memberId;
    private String firstName;
    private String lastName;
    private String email;
    private String provider;

    private Boolean needAdditionalInfo; // 소셜 로그인 후 추가 정보 입력 필요 여부


    public LoginResponse(String token, Long memberId, String firstName, String lastName, String email, String provider) {
        this.token = token;
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.provider = provider;
        this.needAdditionalInfo = false;
    }
}