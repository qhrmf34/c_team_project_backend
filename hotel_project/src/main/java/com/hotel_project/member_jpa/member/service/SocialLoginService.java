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

        // LoginResponse 생성
        LoginResponse response = new LoginResponse(
                token,
                member.getId(),
                member.getFirstName(),
                member.getLastName(),
                member.getEmail(),
                member.getProvider().toString()
        );

        // needAdditionalInfo 설정 (구글은 이메일이 있으므로 전화번호만 체크)
        boolean needAdditionalInfo;
        if (provider == Provider.google) {
            // 구글은 전화번호만 체크
            needAdditionalInfo = (member.getPhoneNumber() == null || member.getPhoneNumber().isEmpty());
        } else {
            // 카카오, 네이버는 이메일과 전화번호 모두 체크
            needAdditionalInfo =
                    (member.getEmail() == null || member.getEmail().isEmpty()) ||
                            (member.getPhoneNumber() == null || member.getPhoneNumber().isEmpty());
        }

        response.setNeedAdditionalInfo(needAdditionalInfo);

        return response;
    }

    /**
     * 기존 소셜 로그인 처리 메서드
     */
    public MemberEntity processSocialLogin(OAuth2User oauth2User, Provider provider) {
        SocialUserInfo userInfo = extractUserInfo(oauth2User, provider);
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

        return new SocialUserInfo(
                providerId,
                nickname != null ? nickname : "카카오사용자",
                "",
                null // 카카오는 이메일 제공 안함
        );
    }

    // 구글 사용자 정보 추출
    private SocialUserInfo extractGoogleUserInfo(OAuth2User oauth2User) {
        String providerId = oauth2User.getAttribute("sub").toString();
        String name = oauth2User.getAttribute("name");
        String givenName = oauth2User.getAttribute("given_name");
        String familyName = oauth2User.getAttribute("family_name");
        String email = oauth2User.getAttribute("email"); // 구글은 이메일 제공

        return new SocialUserInfo(
                providerId,
                givenName != null ? givenName : (name != null ? name : "구글사용자"),
                familyName != null ? familyName : "",
                email // 구글은 이메일 포함
        );
    }

    // 네이버 사용자 정보 추출
    private SocialUserInfo extractNaverUserInfo(OAuth2User oauth2User) {
        Map<String, Object> response = (Map<String, Object>) oauth2User.getAttribute("response");
        String providerId = (String) response.get("id");
        String name = (String) response.get("name");
        String nickname = (String) response.get("nickname");

        return new SocialUserInfo(
                providerId,
                nickname != null ? nickname : (name != null ? name : "네이버사용자"),
                "",
                null // 네이버는 이메일 제공 안함 (또는 필요시 추출 가능)
        );
    }

    // 공통 소셜 로그인 처리 로직
    private MemberEntity processSocialLoginCommon(SocialUserInfo userInfo, Provider provider) {
        System.out.println("=== " + provider + " 로그인 처리 시작 ===");
        System.out.println("Provider ID: " + userInfo.getProviderId());
        System.out.println("이름: " + userInfo.getFirstName());

        // MyBatis로 기존 회원 확인
        MemberDto existingMember = memberMapper.findByProviderAndProviderId(provider, userInfo.getProviderId());

        System.out.println("기존 회원 조회 결과: " + (existingMember != null));

        if (existingMember != null) {
            return updateExistingMember(existingMember, userInfo);
        } else {
            return createNewMember(userInfo, provider);
        }
    }

    // 기존 회원 업데이트 - firstName, lastName만 업데이트
    private MemberEntity updateExistingMember(MemberDto existingMemberDto, SocialUserInfo userInfo) {
        System.out.println("=== 기존 회원 업데이트 ===");
        System.out.println("기존 회원 ID: " + existingMemberDto.getId());

        MemberEntity existingMember = new MemberEntity();
        existingMember.copyMembers(existingMemberDto);

        // firstName, lastName만 업데이트 (이메일, 전화번호는 기존 값 유지)
        existingMember.setFirstName(userInfo.getFirstName());
        existingMember.setLastName(userInfo.getLastName());
        existingMember.setUpdatedAt(LocalDateTime.now());

        MemberEntity savedMember = memberRepository.save(existingMember);
        System.out.println("기존 회원 업데이트 완료 - ID: " + savedMember.getId());
        return savedMember;
    }

    // 신규 회원 생성
    private MemberEntity createNewMember(SocialUserInfo userInfo, Provider provider) {
        System.out.println("=== 신규 회원 생성 ===");

        // 이메일이 있으면 중복 체크 (구글 포함, 모든 provider)
        if (userInfo.getEmail() != null) {
            if (memberRepository.existsByEmail(userInfo.getEmail())) {
                throw new IllegalStateException("이미 사용 중인 이메일입니다: " + userInfo.getEmail());
            }
        }

        MemberEntity newMember = new MemberEntity();
        newMember.setFirstName(userInfo.getFirstName());
        newMember.setLastName(userInfo.getLastName());

        // 구글인 경우에만 이메일 설정
        if (provider == Provider.google && userInfo.getEmail() != null) {
            newMember.setEmail(userInfo.getEmail());
        } else {
            newMember.setEmail(null);
        }

        newMember.setProvider(provider);
        newMember.setProviderId(userInfo.getProviderId());
        newMember.setPassword(null);
        newMember.setPhoneNumber(null);
        newMember.setCreatedAt(LocalDateTime.now());
        newMember.setUpdatedAt(LocalDateTime.now());

        System.out.println("신규 회원 Provider ID: " + newMember.getProviderId());

        MemberEntity savedMember = memberRepository.save(newMember);
        System.out.println("신규 회원 생성 완료 - ID: " + savedMember.getId());
        return savedMember;
    }

    // 소셜 사용자 정보를 담는 내부 클래스 (이메일 추가)
    private static class SocialUserInfo {
        private final String providerId;
        private final String firstName;
        private final String lastName;
        private final String email; // 이메일 추가

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