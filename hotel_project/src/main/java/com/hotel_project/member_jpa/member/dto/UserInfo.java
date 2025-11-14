package com.hotel_project.member_jpa.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String provider;
}