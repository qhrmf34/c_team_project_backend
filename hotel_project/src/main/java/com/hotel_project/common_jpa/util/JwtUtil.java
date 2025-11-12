package com.hotel_project.common_jpa.util;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt.secret:hotel_project_secret_key_for_jwt_token_generation}")
    private String secretKey;

    @Value("${jwt.expiration:86400}") // 1일 (초 단위)
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * JWT 토큰 생성
     * @param memberId 회원 ID
     * @param provider 로그인 제공자 (local, google, kakao, naver)
     * @return JWT 토큰
     */
    public String generateToken(Long memberId, String provider) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusSeconds(expiration);

        // provider가 소셜인지 확인
        String tokenType;
        if (provider.equals("google") || provider.equals("kakao") || provider.equals("naver")) {
            tokenType = "social_login";  // 소셜 로그인
        } else {
            tokenType = "access";  // 일반 로그인
        }

        return Jwts.builder()
                .setSubject(memberId.toString())
                .claim("provider", provider)
                .claim("type", tokenType)
                .claim("jti", UUID.randomUUID().toString())
                .claim("iat_timestamp", now.toString())
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expiry.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 소셜 로그인 신규 회원용 임시 JWT 토큰 생성
     * DB에 저장되기 전 signup 페이지에서 사용하는 토큰
     * @param providerId 소셜 로그인 provider ID
     * @param provider 소셜 로그인 제공자 (google, kakao, naver)
     * @param email 이메일 (Google만 제공)
     * @param firstName 이름
     * @param lastName 성
     * @return 임시 JWT 토큰
     */
    public String generateSocialSignupToken(String providerId, String provider, String email, String firstName, String lastName) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusHours(1); // 1시간 유효

        return Jwts.builder()
                .setSubject(providerId) // providerId를 subject로
                .claim("provider", provider)
                .claim("type", "social_signup") // 임시 토큰 타입
                .claim("email", email)
                .claim("firstName", firstName)
                .claim("lastName", lastName)
                .claim("jti", UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expiry.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * JWT 토큰에서 회원 ID 추출
     * @param token JWT 토큰
     * @return 회원 ID
     */
    public Long getMemberIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * JWT 토큰에서 providerId 추출 (소셜 회원가입 토큰용)
     * @param token JWT 토큰
     * @return providerId
     */
    public String getProviderIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject(); // 소셜 회원가입 토큰은 subject가 providerId
    }

    /**
     * JWT 토큰에서 Provider 추출
     * @param token JWT 토큰
     * @return Provider
     */
    public String getProviderFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("provider", String.class);
    }

    /**
     * JWT 토큰에서 이메일 추출 (소셜 회원가입 토큰용)
     * @param token JWT 토큰
     * @return 이메일
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("email", String.class);
    }

    /**
     * JWT 토큰에서 firstName 추출 (소셜 회원가입 토큰용)
     * @param token JWT 토큰
     * @return firstName
     */
    public String getFirstNameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("firstName", String.class);
    }

    /**
     * JWT 토큰에서 lastName 추출 (소셜 회원가입 토큰용)
     * @param token JWT 토큰
     * @return lastName
     */
    public String getLastNameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("lastName", String.class);
    }

    /**
     * JWT 토큰 타입 확인
     * @param token JWT 토큰
     * @return 토큰 타입 (access, social_signup 등)
     */
    public String getTokenType(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("type", String.class);
    }

    /**
     * 소셜 회원가입 토큰인지 확인
     * @param token JWT 토큰
     * @return 소셜 회원가입 토큰이면 true
     */
    public boolean isSocialSignupToken(String token) {
        try {
            String type = getTokenType(token);
            return "social_signup".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * JWT 토큰에서 JWT ID 추출
     * @param token JWT 토큰
     * @return JWT ID
     */
    public String getJwtIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("jti", String.class);
    }

    /**
     * JWT 토큰의 유효성 검증
     * @param token JWT 토큰
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT 토큰이 만료되었습니다: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT 토큰입니다: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("잘못된 JWT 토큰입니다: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT 토큰이 null이거나 비어있습니다: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("JWT 토큰 검증 중 오류가 발생했습니다: " + e.getMessage());
        }
        return false;
    }

    /**
     * JWT 토큰의 만료 여부 확인
     * @param token JWT 토큰
     * @return 만료되었으면 true, 그렇지 않으면 false
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * JWT 토큰에서 Claims 추출
     * @param token JWT 토큰
     * @return Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Authorization 헤더에서 토큰 추출
     */
    public String extractToken(String authorization) throws CommonExceptionTemplate {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new CommonExceptionTemplate(401, "Bearer 토큰이 필요합니다.");
        }
        return authorization.substring(7);
    }
}