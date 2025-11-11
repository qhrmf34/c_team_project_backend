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
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2User oauth2User = super.loadUser(userRequest);

        processUserWithToken(oauth2User, registrationId);

        return oauth2User;
    }

    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OidcUser oidcUser = oidcUserService.loadUser(userRequest);

        processUserWithToken(oidcUser, registrationId);

        return oidcUser;
    }

    private void processUserWithToken(OAuth2User oauth2User, String registrationId) {
        try {
            Provider provider = getProviderFromRegistrationId(registrationId);
            if (provider == null) return;

            LoginResponse loginResponse = socialLoginService.processSocialLoginWithToken(oauth2User, provider);

            if (loginResponse != null) {
                saveLoginResponseToSession(loginResponse);
            }

        } catch (Exception e) {
            // 로거로 기록 (실제 운영 환경에서)
        }
    }

    private Provider getProviderFromRegistrationId(String registrationId) {
        switch (registrationId.toLowerCase()) {
            case "google":
                return Provider.google;
            case "kakao":
                return Provider.kakao;
            case "naver":
                return Provider.naver;
            default:
                return null;
        }
    }

    private void saveLoginResponseToSession(LoginResponse loginResponse) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attr.getRequest();
        request.getSession().setAttribute("loginResponse", loginResponse);
    }
}