package com.hotel_project.member_jpa.member.service;

import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.member_jpa.member.dto.*;
import com.hotel_project.member_jpa.member.mapper.MemberMapper;
import com.hotel_project.member_jpa.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Transactional
public class SocialLoginService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse processSocialLoginWithToken(OAuth2User oauth2User, Provider provider) {
        SocialUserInfo userInfo = extractUserInfo(oauth2User, provider);

        MemberDto existingMember = memberMapper.findByProviderAndProviderId(provider, userInfo.getProviderId());

        if (existingMember != null) {
            return handleExistingMember(existingMember, provider);
        } else {
            return handleNewMember(userInfo, provider);
        }
    }

    private LoginResponse handleExistingMember(MemberDto existingMember, Provider provider) {
        String token = jwtUtil.generateToken(existingMember.getId(), provider.toString());
        boolean needsAdditionalInfo = checkNeedAdditionalInfo(existingMember);

        return LoginResponse.builder()
                .token(token)
                .memberId(existingMember.getId())
                .firstName(existingMember.getFirstName())
                .lastName(existingMember.getLastName())
                .email(existingMember.getEmail())
                .provider(provider.toString())
                .needAdditionalInfo(needsAdditionalInfo)
                .build();
    }

    private LoginResponse handleNewMember(SocialUserInfo userInfo, Provider provider) {
        String tempToken = jwtUtil.generateSocialSignupToken(
                userInfo.getProviderId(),
                provider.toString(),
                userInfo.getEmail(),
                userInfo.getFirstName(),
                userInfo.getLastName()
        );

        return LoginResponse.builder()
                .token(tempToken)
                .memberId(null)
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .email(userInfo.getEmail())
                .provider(provider.toString())
                .needAdditionalInfo(true)
                .build();
    }

    private boolean checkNeedAdditionalInfo(MemberDto member) {
// 전화번호와 도로명 주소만 필수(상세주소는 선택)
        return isNullOrEmpty(member.getPhoneNumber()) ||
                isNullOrEmpty(member.getRoadAddress());
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private SocialUserInfo extractUserInfo(OAuth2User oauth2User, Provider provider) {
        switch (provider) {
            case kakao:
                return extractKakaoUserInfo(oauth2User);
            case google:
                return extractGoogleUserInfo(oauth2User);
            case naver:
                return extractNaverUserInfo(oauth2User);
            default:
                throw new IllegalArgumentException("지원하지 않는 소셜 로그인: " + provider);
        }
    }

    private SocialUserInfo extractKakaoUserInfo(OAuth2User oauth2User) {
        String providerId = oauth2User.getAttribute("id").toString();
        Map<String, Object> kakaoAccount = (Map<String, Object>) oauth2User.getAttribute("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (String) profile.get("nickname");
        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;

        return new SocialUserInfo(
                providerId,
                nickname != null ? nickname : "카카오사용자",
                "",
                email
        );
    }

    private SocialUserInfo extractGoogleUserInfo(OAuth2User oauth2User) {
        String providerId = oauth2User.getAttribute("sub").toString();
        String name = oauth2User.getAttribute("name");
        String givenName = oauth2User.getAttribute("given_name");
        String familyName = oauth2User.getAttribute("family_name");
        String email = oauth2User.getAttribute("email");

        String firstName = givenName;
        if (isNullOrEmpty(firstName)) {
            firstName = !isNullOrEmpty(name) ? name : "구글사용자";
        }

        String lastName = !isNullOrEmpty(familyName) ? familyName : null;

        return new SocialUserInfo(providerId, firstName, lastName, email);
    }

    private SocialUserInfo extractNaverUserInfo(OAuth2User oauth2User) {
        Map<String, Object> response = (Map<String, Object>) oauth2User.getAttribute("response");
        String providerId = (String) response.get("id");
        String name = (String) response.get("name");
        String nickname = (String) response.get("nickname");
        String email = (String) response.get("email");

        return new SocialUserInfo(
                providerId,
                nickname != null ? nickname : (name != null ? name : "네이버사용자"),
                "",
                email
        );
    }

    private static class SocialUserInfo {
        private final String providerId;
        private final String firstName;
        private final String lastName;
        private final String email;

        public SocialUserInfo(String providerId, String firstName, String lastName, String email) {
            this.providerId = providerId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }

        public String getProviderId() { return providerId; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getEmail() { return email; }
    }
}