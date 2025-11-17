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

    @Value("${jwt.expiration:86400}") // 1일(초 단위)
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * ✅ JWT 토큰 생성 (memberId만 포함)
     * @param memberId 회원 ID
     * @return JWT 토큰
     */
    public String generateToken(Long memberId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusSeconds(expiration);

        return Jwts.builder()
                .setSubject(memberId.toString())
                .claim("jti", UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expiry.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * ✅ 소셜 로그인 신규 회원용 임시 JWT 토큰 생성
     * providerId, provider와 함께 사용자 기본 정보 포함 (DB 저장 전이라 예외적으로 정보 포함)
     * @param providerId 소셜 로그인 provider ID
     * @param provider 소셜 로그인 제공자 (google, kakao, naver)
     * @param email 이메일
     * @param firstName 이름
     * @param lastName 성
     * @return 임시 JWT 토큰
     */
    public String generateSocialSignupToken(String providerId, String provider,
                                            String email, String firstName, String lastName) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusHours(1); // 1시간 유효

        return Jwts.builder()
                .setSubject(providerId)
                .claim("provider", provider)
                .claim("type", "social_signup")
                .claim("email", email != null ? email : "")
                .claim("firstName", firstName != null ? firstName : "")
                .claim("lastName", lastName != null ? lastName : "")
                .claim("jti", UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .setExpiration(Date.from(expiry.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * ✅ JWT 토큰에서 회원 ID 추출
     */
    public Long getMemberIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * ✅ JWT 토큰에서 providerId 추출 (소셜 회원가입 토큰용)
     */
    public String getProviderIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * ✅ JWT 토큰에서 특정 Claim 추출
     */
    private String getClaimFromToken(String token, String claimKey) {
        Claims claims = getClaimsFromToken(token);
        return claims.get(claimKey, String.class);
    }

    /**
     * ✅ JWT 토큰에서 Provider 추출 (소셜 회원가입 토큰용)
     */
    public String getProviderFromToken(String token) {
        return getClaimFromToken(token, "provider");
    }

    /**
     * ✅ JWT 토큰에서 이메일 추출 (소셜 회원가입 토큰용)
     */
    public String getEmailFromToken(String token) {
        return getClaimFromToken(token, "email");
    }

    /**
     * ✅ JWT 토큰에서 firstName 추출 (소셜 회원가입 토큰용)
     */
    public String getFirstNameFromToken(String token) {
        return getClaimFromToken(token, "firstName");
    }

    /**
     * ✅ JWT 토큰에서 lastName 추출 (소셜 회원가입 토큰용)
     */
    public String getLastNameFromToken(String token) {
        return getClaimFromToken(token, "lastName");
    }

    /**
     * ✅ JWT 토큰 타입 확인
     */
    public String getTokenType(String token) {
        return getClaimFromToken(token, "type");
    }

    /**
     * ✅ JWT 토큰에서 JWT ID 추출
     */
    public String getJwtIdFromToken(String token) {
        return getClaimFromToken(token, "jti");
    }

    /**
     * ✅ 소셜 회원가입 토큰인지 확인
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
     * ✅ JWT 토큰의 유효성 검증
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
     * ✅ JWT 토큰의 만료 여부 확인
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
     * ✅ JWT 토큰에서 Claims 추출
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * ✅ Authorization 헤더에서 토큰 추출
     */
    public String extractToken(String authorization) throws CommonExceptionTemplate {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new CommonExceptionTemplate(401, "Bearer 토큰이 필요합니다.");
        }
        return authorization.substring(7);
    }
}