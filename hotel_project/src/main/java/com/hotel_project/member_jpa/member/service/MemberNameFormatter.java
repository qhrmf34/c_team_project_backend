package com.hotel_project.member_jpa.member.service;

import org.springframework.stereotype.Component;

@Component
public class MemberNameFormatter {

    public String formatDisplayName(String provider, String firstName, String lastName, String email) {
        if (provider == null) {
            return "Anonymous";
        }

        switch (provider.toLowerCase()) {
            case "leave":
                return "탈퇴한 회원";

            case "local":
                // local: firstName + lastName (이름 + 성)
                return formatLocalName(firstName, lastName, email);

            case "google":
                // google: lastName + firstName (성 + 이름)
                return formatGoogleName(firstName, lastName, email);

            case "kakao":
                return formatSingleName(firstName, email, "Kakao User");

            case "naver":
                return formatSingleName(firstName, email, "Naver User");

            default:
                return firstName != null ? firstName : "Anonymous";
        }
    }

    private String formatLocalName(String firstName, String lastName, String email) {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        if (firstName != null) {
            return firstName;
        }
        if (lastName != null) {
            return lastName;
        }
        if (email != null) {
            return email.split("@")[0];
        }
        return "User";
    }

    private String formatGoogleName(String firstName, String lastName, String email) {
        if (lastName != null && firstName != null) {
            return lastName + " " + firstName;
        }
        if (firstName != null) {
            return firstName;
        }
        if (lastName != null) {
            return lastName;
        }
        if (email != null) {
            return email.split("@")[0];
        }
        return "Google User";
    }

    private String formatSingleName(String firstName, String email, String defaultName) {
        if (firstName != null) {
            return firstName;
        }
        if (email != null) {
            return email.split("@")[0];
        }
        return defaultName;
    }
}