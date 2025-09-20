package com.hotel_project.member_jpa.member.service;

import com.hotel_project.member_jpa.member.dto.MemberEntity;
import com.hotel_project.member_jpa.member.dto.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private SocialLoginService socialLoginService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        System.out.println("=== OAuth2 사용자 정보 로드 ===");
        System.out.println("제공자: " + userRequest.getClientRegistration().getRegistrationId());
        System.out.println("사용자 정보: " + oauth2User.getAttributes());

        // 카카오 로그인인 경우 DB에 저장
        if ("kakao".equals(userRequest.getClientRegistration().getRegistrationId())) {
            try {
                MemberEntity member = socialLoginService.processSocialLogin(oauth2User, Provider.kakao);
                System.out.println("DB 저장 완료 - 회원 ID: " + member.getId());
            } catch (Exception e) {
                System.out.println("DB 저장 실패: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return oauth2User;
    }
}