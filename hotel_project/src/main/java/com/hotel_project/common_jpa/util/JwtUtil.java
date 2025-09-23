package com.hotel_project.common_jpa.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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

        return Jwts.builder()
                .setSubject(memberId.toString())
                .claim("provider", provider)
                .claim("type", "access")
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
     * JWT 토큰에서 Provider 추출
     * @param token JWT 토큰
     * @return Provider
     */
    public String getProviderFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("provider", String.class);
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
     * HTTP Authorization 헤더에서 토큰 추출
     * @param authorizationHeader Authorization 헤더 값
     * @return JWT 토큰 (Bearer 접두사 제거)
     */
    public String extractTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}