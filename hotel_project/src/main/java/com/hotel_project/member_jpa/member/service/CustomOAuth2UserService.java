package com.hotel_project.member_jpa.member.service;

import com.hotel_project.member_jpa.member.dto.MemberEntity;
import com.hotel_project.member_jpa.member.dto.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private SocialLoginService socialLoginService;

    private final OidcUserService oidcUserService = new OidcUserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("★★★ CustomOAuth2UserService.loadUser() 호출됨 ★★★");

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("Provider: " + registrationId);

        // 기본 OAuth2 처리 (Kakao, Naver 등)
        OAuth2User oauth2User = super.loadUser(userRequest);

        // DB 저장 처리
        processUser(oauth2User, registrationId);

        return oauth2User;
    }

    // Google(OIDC) 전용 메서드 추가
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("★★★ OIDC loadUser() 호출됨 (Google) ★★★");

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("Provider: " + registrationId);

        // OIDC 처리
        OidcUser oidcUser = oidcUserService.loadUser(userRequest);

        // DB 저장 처리
        processUser(oidcUser, registrationId);

        return oidcUser;
    }

    // 공통 처리 로직
    private void processUser(OAuth2User oauth2User, String registrationId) {
        try {
            Provider provider = null;

            switch (registrationId.toLowerCase()) {
                case "google":
                    provider = Provider.google;
                    break;
                case "kakao":
                    provider = Provider.kakao;
                    break;
                case "naver":
                    provider = Provider.naver;
                    break;
                default:
                    System.out.println("지원하지 않는 제공자: " + registrationId);
                    return;
            }

            System.out.println("DB 저장 시작 - Provider: " + provider);
            MemberEntity member = socialLoginService.processSocialLogin(oauth2User, provider);

            if (member != null) {
                System.out.println("✓ DB 저장 성공! 회원 ID: " + member.getId());
            }

        } catch (Exception e) {
            System.out.println("DB 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}