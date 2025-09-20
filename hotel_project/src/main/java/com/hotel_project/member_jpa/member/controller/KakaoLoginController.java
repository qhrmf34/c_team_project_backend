package com.hotel_project.member_jpa.member.controller;

import com.hotel_project.member_jpa.member.dto.MemberEntity;
import com.hotel_project.member_jpa.member.dto.Provider;
import com.hotel_project.member_jpa.member.service.SocialLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/auth/kakao")
public class KakaoLoginController {

    @Autowired
    private SocialLoginService socialLoginService;

    // 카카오 로그인 시작 (이건 유지)
    @GetMapping("/login")
    public String kakaoLogin() {
        System.out.println("=== 카카오 로그인 시작 ===");
        return "redirect:/oauth2/authorization/kakao";
    }

    // 수동 콜백 제거 - Spring Security가 자동 처리
    // @GetMapping("/callback") 이 부분 완전 삭제

    // 카카오 사용자 정보 API (Ajax 호출용)
    @GetMapping("/user")
    @ResponseBody
    public MemberEntity getKakaoUser(OAuth2AuthenticationToken authentication) {
        if (authentication != null && "kakao".equals(authentication.getAuthorizedClientRegistrationId())) {
            OAuth2User oauth2User = authentication.getPrincipal();
            return socialLoginService.processSocialLogin(oauth2User, Provider.kakao);
        }
        return null;
    }

    // 테스트용
    @GetMapping("/status")
    @ResponseBody
    public String getKakaoStatus(OAuth2AuthenticationToken authentication) {
        if (authentication != null && "kakao".equals(authentication.getAuthorizedClientRegistrationId())) {
            return "카카오 로그인됨 - ID: " + authentication.getPrincipal().getAttribute("id");
        }
        return "카카오 로그인 안됨";
    }
}