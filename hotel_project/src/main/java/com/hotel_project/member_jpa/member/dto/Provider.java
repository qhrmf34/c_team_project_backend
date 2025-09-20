package com.hotel_project.member_jpa.member.dto;

public enum Provider {
    local, google, kakao, naver;

    public boolean isKakao() {
        return this == kakao;
    }

    public boolean isGoogle() {
        return this == google;
    }

    public boolean isNaver() {
        return this == naver;
    }

    public boolean isLocal() {
        return this == local;
    }

}
