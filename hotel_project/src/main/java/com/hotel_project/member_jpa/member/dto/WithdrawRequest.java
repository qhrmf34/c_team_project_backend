package com.hotel_project.member_jpa.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequest {
    private String password;  // 로컬 계정만 필요
}