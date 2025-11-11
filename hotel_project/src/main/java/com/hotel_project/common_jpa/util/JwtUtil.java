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

    @Value("${jwt.expiration:86400}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(Long memberId, String provider) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusSeconds(expiration);

        String tokenType = isSocialProvider(provider) ? "social_login" : "access";

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

    public String generateSocialSignupToken(String providerId, String provider, String email,
                                            String firstName, String lastName) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusHours(1);

        return Jwts.builder()
                .setSubject(providerId)
                .claim("provider", provider)
                .claim("type", "social_signup")
                .claim("email", email)
                .claim("firstName", firstName)
                .claim("lastName", lastName)
                .claim("jti", UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expiry.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Long getMemberIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getProviderIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public String getProviderFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("provider", String.class);
    }

    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("email", String.class);
    }

    public String getFirstNameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("firstName", String.class);
    }

    public String getLastNameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("lastName", String.class);
    }

    public String getTokenType(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("type", String.class);
    }

    public boolean isSocialSignupToken(String token) {
        try {
            String type = getTokenType(token);
            return "social_signup".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    public String getJwtIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("jti", String.class);
    }

    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            // 로그는 실제 운영 환경에서 로거로 기록
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            // 로그는 실제 운영 환경에서 로거로 기록
        }
        return false;
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public String extractToken(String authorization) throws CommonExceptionTemplate {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new CommonExceptionTemplate(401, "Bearer 토큰이 필요합니다.");
        }
        return authorization.substring(7);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isSocialProvider(String provider) {
        return provider.equals("google") || provider.equals("kakao") || provider.equals("naver");
    }
}