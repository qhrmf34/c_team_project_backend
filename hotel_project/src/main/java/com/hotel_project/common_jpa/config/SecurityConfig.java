package com.hotel_project.common_jpa.config;

import com.hotel_project.member_jpa.member.dto.LoginResponse;
import com.hotel_project.member_jpa.member.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${FRONTEND_URL:http://localhost:8080}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ★★★ OAuth2 로그인을 위해 세션 정책 수정 ★★★
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // STATELESS -> IF_REQUIRED로 변경
                        .maximumSessions(1) // 동시 세션 1개로 제한
                        .maxSessionsPreventsLogin(false) // 새 로그인 시 기존 세션 무효화
                )
                .authorizeHttpRequests(auth -> auth
                                .anyRequest().permitAll()
                        // ========== 보안 설정 주석 처리 (개발용) ==========
                        // 모든 요청 허용 - 로그인하지 않아도 모든 페이지 접근 가능

                        // ========== 운영 시 사용할 보안 설정 (주석 처리됨) ==========
                        // 인증이 필요 없는 경로들
                        // .requestMatchers("/", "/oauth2/**", "/login/**", "/auth/**").permitAll()
                        // .requestMatchers("/api/member/signup", "/api/member/login").permitAll()  // 일반 회원가입/로그인
                        // .requestMatchers("/api/member/forgot-password", "/api/member/verify-reset-code", "/api/member/reset-password").permitAll()  // 비밀번호 재설정
                        // .requestMatchers("/api/test/**").permitAll()  // 테스트 API
                        // .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Swagger

                        // 인증이 필요한 경로들
                        // .requestMatchers("/api/member/profile/**").authenticated()  // 회원 프로필 관련
                        // .requestMatchers("/api/**").authenticated()  // 기타 API들

                        // .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOAuth2UserService::loadUser)
                                .userService(customOAuth2UserService)
                        )
                        // ★★★ successHandler에서 토큰 처리 ★★★
                        .successHandler((request, response, authentication) -> {
                            System.out.println("=== 소셜 로그인 성공 핸들러 실행 ===");
                            System.out.println("인증 객체: " + authentication.getName());
                            System.out.println("사용할 프론트엔드 URL: " + frontendUrl);

                            try {
                                // CustomOAuth2UserService에서 생성된 토큰과 사용자 정보 가져오기
                                LoginResponse loginResponse = (LoginResponse) request.getSession().getAttribute("loginResponse");

                                if (loginResponse != null) {
                                    // 사용자 정보를 JSON 문자열로 변환
                                    String userInfoJson = String.format(
                                            "{\"id\":%d,\"firstName\":\"%s\",\"lastName\":\"%s\",\"email\":\"%s\",\"provider\":\"%s\"}",
                                            loginResponse.getMemberId(),
                                            escapeJson(loginResponse.getFirstName()),
                                            escapeJson(loginResponse.getLastName() != null ? loginResponse.getLastName() : ""),
                                            escapeJson(loginResponse.getEmail()),
                                            loginResponse.getProvider()
                                    );

                                    // URL 인코딩
                                    String encodedUserInfo = URLEncoder.encode(userInfoJson, StandardCharsets.UTF_8);

                                    // ★★★ 쿠키에서 returnToPayment 확인 ★★★
                                    String returnToPayment = null;
                                    if (request.getCookies() != null) {
                                        for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                                            if ("returnToPayment".equals(cookie.getName())) {
                                                returnToPayment = cookie.getValue();
                                                break;
                                            }
                                        }
                                    }

                                    String redirectUrl;

                                    // ★★★ 결제 페이지에서 온 경우 /hotelfour로 리다이렉트 ★★★
                                    if ("true".equals(returnToPayment)) {
                                        System.out.println("결제 페이지에서 온 소셜 로그인 - /hotelfour로 리다이렉트");
                                        redirectUrl = String.format(
                                                "%s/hotelfour?login=success&token=%s&userInfo=%s",
                                                frontendUrl,
                                                loginResponse.getToken(),
                                                encodedUserInfo
                                        );
                                    } else {
                                        // ★★★ 일반 로그인은 기존대로 /auth/callback ★★★
                                        System.out.println("일반 소셜 로그인 - /auth/callback으로 리다이렉트");
                                        redirectUrl = String.format(
                                                "%s/auth/callback?token=%s&userInfo=%s",
                                                frontendUrl,
                                                loginResponse.getToken(),
                                                encodedUserInfo
                                        );
                                    }

                                    System.out.println("리다이렉트 URL: " + redirectUrl);

                                    // 세션에서 임시 데이터 제거
                                    request.getSession().removeAttribute("loginResponse");

                                    response.sendRedirect(redirectUrl);
                                } else {
                                    System.out.println("LoginResponse가 세션에 없습니다.");
                                    response.sendRedirect(frontendUrl + "/login?error=no_token");
                                }

                            } catch (Exception e) {
                                System.out.println("리다이렉트 처리 실패: " + e.getMessage());
                                e.printStackTrace();
                                response.sendRedirect(frontendUrl + "/login?error=redirect_failed");
                            }
                        })
                        // ★★★ failureHandler는 단순하게 ★★★
                        .failureHandler((request, response, exception) -> {
                            System.out.println("=== 소셜 로그인 실패 ===");
                            System.out.println("오류: " + exception.getMessage());
                            response.sendRedirect(frontendUrl + "/login?error=oauth_failed");
                        })
                )
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    // JSON 이스케이프 처리
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:8080",
                "http://localhost:3000",
                "http://localhost:180"  // Docker 프론트엔드 포트 추가
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}