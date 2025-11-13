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
                return formatLocalName(firstName, lastName, email);
            case "google":
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
        if (isValid(firstName) && isValid(lastName)) {
            return firstName + " " + lastName;
        }
        return getSingleNameOrDefault(firstName, lastName, email, "User");
    }

    private String formatGoogleName(String firstName, String lastName, String email) {
        if (isValid(lastName) && isValid(firstName)) {
            return lastName + " " + firstName;
        }
        return getSingleNameOrDefault(firstName, lastName, email, "Google User");
    }

    private String formatSingleName(String firstName, String email, String defaultName) {
        if (isValid(firstName)) {
            return firstName;
        }
        if (email != null) {
            return email.split("@")[0];
        }
        return defaultName;
    }

    private String getSingleNameOrDefault(String firstName, String lastName, String email, String defaultName) {
        if (isValid(firstName)) {
            return firstName;
        }
        if (isValid(lastName)) {
            return lastName;
        }
        if (email != null) {
            return email.split("@")[0];
        }
        return defaultName;
    }

    private boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }
}