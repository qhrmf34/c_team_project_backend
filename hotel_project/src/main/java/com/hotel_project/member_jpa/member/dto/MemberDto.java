package com.hotel_project.member_jpa.member.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDto implements IMember {

    private Long id;

    @NotBlank(message = "이름은 필수 입력입니다.")
    @Size(min = 1, max = 50, message = "이름은 1자 이상 50자 이하로 입력해야 합니다.")
    private String firstName;

    @NotBlank(message = "성은 필수 입력입니다.")
    @Size(min = 1, max = 50, message = "성은 1자 이상 50자 이하로 입력해야 합니다.")
    private String lastName;

    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 100자 이하로 입력해야 합니다.")
    private String email;

    private String password;  //수정필요

    @Pattern(regexp = "^[0-9-+()\\s]*$", message = "올바른 전화번호 형식이 아닙니다.")
    @Size(max = 20, message = "전화번호는 20자 이하로 입력해야 합니다.")
    private String phoneNumber;

    @Size(max = 500, message = "주소는 500자 이하로 입력해야 합니다.")
    private String address;

    @Size(max = 50, message = "제공자는 50자 이하로 입력해야 합니다.")
    private String provider;

    @Size(max = 100, message = "제공자 ID는 100자 이하로 입력해야 합니다.")
    private String providerId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}