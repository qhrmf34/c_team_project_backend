package com.hotel_project.member_jpa.member.service;

import com.hotel_project.member_jpa.member.dto.LoginResponse;
import com.hotel_project.member_jpa.member.dto.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final SocialLoginService socialLoginService;
    private final OidcUserService oidcUserService = new OidcUserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("★★★ CustomOAuth2UserService.loadUser() 호출됨 ★★★");

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("Provider: " + registrationId);

        // 기본 OAuth2 처리 (Kakao, Naver 등)
        OAuth2User oauth2User = super.loadUser(userRequest);

        // ⭐ 수정: 기존 회원만 로그인, 신규 회원은 임시 JWT만
        processUserWithToken(oauth2User, registrationId);

        return oauth2User;
    }

    // Google(OIDC) 전용 메서드
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("★★★ OIDC loadUser() 호출됨 (Google) ★★★");

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("Provider: " + registrationId);

        // OIDC 처리
        OidcUser oidcUser = oidcUserService.loadUser(userRequest);

        // ⭐ 수정: 기존 회원만 로그인, 신규 회원은 임시 JWT만
        processUserWithToken(oidcUser, registrationId);

        return oidcUser;
    }

    // ⭐ 수정된 공통 처리 로직
    private void processUserWithToken(OAuth2User oauth2User, String registrationId) {
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

            System.out.println("소셜 로그인 처리 시작 - Provider: " + provider);

            // ⭐ 변경: 기존 회원 확인 후 처리 (신규 회원은 DB 저장 안 함)
            LoginResponse loginResponse = socialLoginService.processSocialLoginWithToken(oauth2User, provider);

            if (loginResponse != null) {
                if (loginResponse.getMemberId() != null) {
                    // 기존 회원 - 정식 로그인
                    System.out.println("✓ 기존 회원 로그인 성공! 회원 ID: " + loginResponse.getMemberId());
                    System.out.println("✓ JWT 토큰 생성 완료: " + loginResponse.getToken().substring(0, 20) + "...");
                } else {
                    // 신규 회원 - 임시 JWT만 발급 (DB 저장 안 함)
                    System.out.println("✓ 신규 회원 - 임시 JWT 발급 완료 (DB 미저장)");
                    System.out.println("✓ 임시 JWT 토큰: " + loginResponse.getToken().substring(0, 20) + "...");
                }

                // 세션에 LoginResponse 저장
                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                HttpServletRequest request = attr.getRequest();
                request.getSession().setAttribute("loginResponse", loginResponse);

                System.out.println("✓ 세션에 LoginResponse 저장 완료");
            }

        } catch (Exception e) {
            System.out.println("소셜 로그인 처리 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}