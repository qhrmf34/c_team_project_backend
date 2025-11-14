package com.hotel_project.common_jpa.config;

import com.hotel_project.member_jpa.member.dto.LoginResponse;
import com.hotel_project.member_jpa.member.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
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

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${FRONTEND_URL:http://localhost:8080}")
    private String frontendUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOAuth2UserService::loadUser)
                                .userService(customOAuth2UserService)
                        )
                        .successHandler((request, response, authentication) -> {
                            System.out.println("=== 소셜 로그인 성공 핸들러 실행 ===");
                            System.out.println("인증 객체: " + authentication.getName());
                            System.out.println("사용할 프론트엔드 URL: " + frontendUrl);

                            try {
                                LoginResponse loginResponse = (LoginResponse) request.getSession().getAttribute("loginResponse");

                                if (loginResponse != null) {
                                    System.out.println("✓ 세션에서 LoginResponse 가져옴");
                                    System.out.println("✓ Token: " + (loginResponse.getToken() != null ? loginResponse.getToken().substring(0, 20) + "..." : "null"));
                                    System.out.println("✓ MemberId: " + loginResponse.getMemberId());
                                    System.out.println("✓ NeedAdditionalInfo: " + loginResponse.getNeedAdditionalInfo());

                                    Boolean needAdditionalInfo = loginResponse.getNeedAdditionalInfo();

                                    // 쿠키에서 returnToPayment 확인
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

                                    if ("true".equals(returnToPayment)) {
                                        System.out.println("→ 결제 페이지에서 온 소셜 로그인");
                                        redirectUrl = String.format(
                                                "%s/hotelfour?login=success&token=%s&needAdditionalInfo=%s",
                                                frontendUrl,
                                                loginResponse.getToken(),
                                                needAdditionalInfo != null ? needAdditionalInfo : false
                                        );
                                    } else {
                                        System.out.println("→ 일반 소셜 로그인");
                                        redirectUrl = String.format(
                                                "%s/auth/callback?token=%s&needAdditionalInfo=%s",
                                                frontendUrl,
                                                loginResponse.getToken(),
                                                needAdditionalInfo != null ? needAdditionalInfo : false
                                        );
                                    }

                                    System.out.println("✓ 리다이렉트 URL: " + redirectUrl);

                                    request.getSession().removeAttribute("loginResponse");

                                    response.sendRedirect(redirectUrl);
                                } else {
                                    System.out.println("❌ LoginResponse가 세션에 없습니다!");
                                    response.sendRedirect(frontendUrl + "/login?error=no_token");
                                }

                            } catch (Exception e) {
                                System.out.println("❌ 리다이렉트 처리 실패: " + e.getMessage());
                                e.printStackTrace();
                                response.sendRedirect(frontendUrl + "/login?error=redirect_failed");
                            }
                        })
                        .failureHandler((request, response, exception) -> {
                            System.out.println("=== 소셜 로그인 실패 ===");
                            System.out.println("오류: " + exception.getMessage());
                            exception.printStackTrace();
                            response.sendRedirect(frontendUrl + "/login?error=oauth_failed");
                        })
                )
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:8080",
                "http://localhost:3000",
                "http://localhost:180"
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