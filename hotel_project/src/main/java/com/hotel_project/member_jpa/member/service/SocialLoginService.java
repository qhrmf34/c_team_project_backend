package com.hotel_project.member_jpa.member.service;

import com.hotel_project.member_jpa.member.dto.MemberEntity;
import com.hotel_project.member_jpa.member.dto.Provider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class SocialLoginService {

    @Autowired
    private MemberRepository memberRepository;

    public MemberEntity processSocialLogin(OAuth2User oauth2User, Provider provider) {
        switch (provider) {
            case kakao:
                return processKakaoLogin(oauth2User);
            case google:
              //  return processGoogleLogin(oauth2User);
            case naver:
               // return processNaverLogin(oauth2User);
            default:
                throw new IllegalArgumentException("지원하지 않는 소셜 로그인: " + provider);
        }
    }

    private MemberEntity processKakaoLogin(OAuth2User oauth2User) {
        String kakaoId = oauth2User.getAttribute("id").toString();
        Map<String, Object> kakaoAccount = (Map<String, Object>) oauth2User.getAttribute("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String nickname = (String) profile.get("nickname");

        // 기존 회원 확인
        Optional<MemberEntity> existingMember = memberRepository.findByProviderAndProviderId(Provider.kakao, kakaoId);

        if (existingMember.isPresent()) {
            // 기존 회원 업데이트 - copyNotNullMembers 사용
            MemberEntity member = existingMember.get();

            // 업데이트할 정보만 담은 임시 객체 생성
            MemberEntity updateInfo = new MemberEntity();
            updateInfo.setFirstName(nickname); // 닉네임 업데이트
            updateInfo.setUpdatedAt(LocalDateTime.now()); // 수정일 업데이트

            // null이 아닌 값만 복사 (기존 데이터 보존)
            member.copyNotNullMembers(updateInfo);

            return memberRepository.save(member);

        } else {
            // 신규 회원 생성 - copyMembers 사용
            MemberEntity newMember = new MemberEntity();

            // 신규 회원 정보 설정
            MemberEntity memberInfo = new MemberEntity();
            memberInfo.setFirstName(nickname);
            memberInfo.setLastName(""); // 카카오는 성 정보 없음
            memberInfo.setEmail(kakaoId + "@kakao.temp"); // 임시 이메일
            memberInfo.setProvider(Provider.kakao);
            memberInfo.setProviderId(kakaoId);
            memberInfo.setCreatedAt(LocalDateTime.now());
            memberInfo.setUpdatedAt(LocalDateTime.now());

            // 모든 값 복사
            newMember.copyMembers(memberInfo);

            return memberRepository.save(newMember);
        }
    }
}
