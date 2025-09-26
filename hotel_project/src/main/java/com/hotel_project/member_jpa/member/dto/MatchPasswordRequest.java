package com.hotel_project.member_jpa.member.dto;
import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MatchPasswordRequest {

    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    private String password;
}

