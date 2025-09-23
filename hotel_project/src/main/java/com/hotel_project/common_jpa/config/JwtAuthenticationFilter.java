package com.hotel_project.common_jpa.config;

import com.hotel_project.common_jpa.service.TokenBlacklistService;
import com.hotel_project.common_jpa.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Authorization 헤더에서 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);

            try {
                // 토큰 유효성 검증
                if (jwtUtil.validateToken(token)) {
                    // JWT ID 추출하여 블랙리스트 확인
                    String jwtId = jwtUtil.getJwtIdFromToken(token);

                    // 블랙리스트에 있는 토큰인지 확인
                    if (tokenBlacklistService.isBlacklisted(jwtId)) {
                        logger.warn("블랙리스트에 등록된 토큰입니다: " + jwtId);
                        // 블랙리스트된 토큰은 무효 처리
                    } else {
                        // 토큰에서 사용자 ID 추출
                        Long memberId = jwtUtil.getMemberIdFromToken(token);
                        String provider = jwtUtil.getProviderFromToken(token);

                        // Spring Security 인증 객체 생성
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        memberId, // principal
                                        null,     // credentials
                                        new ArrayList<>() // authorities (권한)
                                );

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // SecurityContext에 인증 정보 설정
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // 요청에 사용자 정보 추가 (컨트롤러에서 사용 가능)
                        request.setAttribute("memberId", memberId);
                        request.setAttribute("provider", provider);
                        request.setAttribute("jwtId", jwtId);
                    }
                }
            } catch (Exception e) {
                // 토큰이 유효하지 않은 경우 로그만 출력하고 계속 진행
                // SecurityContext에 인증 정보가 없으므로 인증 실패로 처리됨
                logger.error("JWT 토큰 검증 실패: " + e.getMessage());
            }
        }

        // 다음 필터로 진행
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // 인증이 필요 없는 경로들
        return path.startsWith("/api/member/signup") ||
                path.startsWith("/api/member/login") ||
                path.startsWith("/api/member/forgot-password") ||
                path.startsWith("/api/member/verify-reset-code") ||
                path.startsWith("/api/member/reset-password") ||
                path.startsWith("/oauth2/") ||
                path.startsWith("/login/") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs/");
    }
}