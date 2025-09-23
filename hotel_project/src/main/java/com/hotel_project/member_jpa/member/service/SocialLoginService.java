package com.hotel_project.member_jpa.member.service;

import com.hotel_project.common_jpa.util.JwtUtil;
import com.hotel_project.member_jpa.member.dto.*;
import com.hotel_project.member_jpa.member.mapper.MemberMapper;
import com.hotel_project.member_jpa.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    /**
     * 소셜 로그인 처리 후 JWT 토큰까지 반환
     */
    public LoginResponse processSocialLoginWithToken(OAuth2User oauth2User, Provider provider) {
        // 기존 로직으로 회원 처리
        MemberEntity member = processSocialLogin(oauth2User, provider);

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(member.getId(), member.getProvider().toString());

        return new LoginResponse(
                token,
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getProvider().toString()
        );
    }

    /**
     * 기존 소셜 로그인 처리 메서드 (하위 호환성 유지)
     */
    public MemberEntity processSocialLogin(OAuth2User oauth2User, Provider provider) {
        // 각 프로바이더별 사용자 정보 추출
        SocialUserInfo userInfo = extractUserInfo(oauth2User, provider);

        // 공통 로그인 처리 로직
        return processSocialLoginCommon(userInfo, provider);
    }

    // 프로바이더별 사용자 정보 추출
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

    // 카카오 사용자 정보 추출
    private SocialUserInfo extractKakaoUserInfo(OAuth2User oauth2User) {
        String providerId = oauth2User.getAttribute("id").toString();
        Map<String, Object> kakaoAccount = (Map<String, Object>) oauth2User.getAttribute("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (String) profile.get("nickname");
        String email = (String) kakaoAccount.get("email");

        return new SocialUserInfo(
                providerId,
                nickname,
                "",  // 카카오는 성 정보 없음
                email != null ? email : "kakao_" + providerId + "@temp.com"
        );
    }

    // 구글 사용자 정보 추출
    private SocialUserInfo extractGoogleUserInfo(OAuth2User oauth2User) {
        String providerId = oauth2User.getAttribute("sub").toString(); // 구글은 sub가 고유 ID
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String givenName = oauth2User.getAttribute("given_name"); // 이름
        String familyName = oauth2User.getAttribute("family_name"); // 성

        return new SocialUserInfo(
                providerId,
                givenName != null ? givenName : name,
                familyName != null ? familyName : "",
                email != null ? email : "google_" + providerId + "@temp.com"
        );
    }

    // 네이버 사용자 정보 추출
    private SocialUserInfo extractNaverUserInfo(OAuth2User oauth2User) {
        Map<String, Object> response = (Map<String, Object>) oauth2User.getAttribute("response");
        String providerId = (String) response.get("id");
        String email = (String) response.get("email");
        String name = (String) response.get("name");
        String nickname = (String) response.get("nickname");

        return new SocialUserInfo(
                providerId,
                nickname != null ? nickname : name,
                "",  // 네이버는 성 정보를 별도로 제공하지 않음
                email != null ? email : "naver_" + providerId + "@temp.com"
        );
    }

    // 공통 소셜 로그인 처리 로직 - MyBatis + JPA 하이브리드
    private MemberEntity processSocialLoginCommon(SocialUserInfo userInfo, Provider provider) {
        System.out.println("=== " + provider + " 로그인 처리 시작 ===");
        System.out.println("Provider ID: " + userInfo.getProviderId());
        System.out.println("이름: " + userInfo.getFirstName());
        System.out.println("이메일: " + userInfo.getEmail());

        // MyBatis로 기존 회원 확인
        MemberDto existingMember = memberMapper.findByProviderAndProviderId(provider, userInfo.getProviderId());

        System.out.println("기존 회원 조회 결과: " + (existingMember != null));

        if (existingMember != null) {
            return updateExistingMember(existingMember, userInfo);
        } else {
            return createNewMember(userInfo, provider);
        }
    }

    // 기존 회원 업데이트 - MyBatis 조회 + JPA 저장
    private MemberEntity updateExistingMember(MemberDto existingMemberDto, SocialUserInfo userInfo) {
        System.out.println("=== 기존 회원 업데이트 ===");
        System.out.println("기존 회원 ID: " + existingMemberDto.getId());

        // DTO를 Entity로 변환
        MemberEntity existingMember = new MemberEntity();
        existingMember.copyMembers(existingMemberDto);

        // 업데이트할 정보 설정
        MemberEntity updateInfo = new MemberEntity();
        updateInfo.setFirstName(userInfo.getFirstName());
        updateInfo.setLastName(userInfo.getLastName());
        updateInfo.setUpdatedAt(LocalDateTime.now());

        // null이 아닌 값만 복사 (기존 데이터 보존)
        existingMember.copyNotNullMembers(updateInfo);

        MemberEntity savedMember = memberRepository.save(existingMember);
        System.out.println("기존 회원 업데이트 완료 - ID: " + savedMember.getId());
        return savedMember;
    }

    // 신규 회원 생성 - JPA 사용
    private MemberEntity createNewMember(SocialUserInfo userInfo, Provider provider) {
        System.out.println("=== 신규 회원 생성 ===");

        MemberEntity newMember = new MemberEntity();
        newMember.setFirstName(userInfo.getFirstName());
        newMember.setLastName(userInfo.getLastName());
        newMember.setEmail(userInfo.getEmail());
        newMember.setProvider(provider);
        newMember.setProviderId(userInfo.getProviderId());
        newMember.setPassword(null); // 소셜 로그인은 비밀번호 없음
        newMember.setPhoneNumber(null); // 소셜 로그인에서는 전화번호 없음
        newMember.setCreatedAt(LocalDateTime.now());
        newMember.setUpdatedAt(LocalDateTime.now());

        System.out.println("신규 회원 이메일: " + newMember.getEmail());
        System.out.println("신규 회원 Provider ID: " + newMember.getProviderId());

        MemberEntity savedMember = memberRepository.save(newMember);
        System.out.println("신규 회원 생성 완료 - ID: " + savedMember.getId());
        return savedMember;
    }

    // 소셜 사용자 정보를 담는 내부 클래스
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