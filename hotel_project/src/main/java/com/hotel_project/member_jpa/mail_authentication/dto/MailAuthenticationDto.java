package com.hotel_project.member_jpa.mail_authentication.dto;



import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailAuthenticationDto implements IMailAuthentication {

    private Long id;

    @NotNull
    private Long memberId;

    @Size(max = 65535) // TEXT 용량 가드
    private String code;

    @NotNull
    private Boolean isVerified;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;

    /* ====== 비즈니스 유틸 ====== */

    /** 만료 여부 */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /** 코드 일치 + 미만료 → 인증 처리 (성공 시 true) */
    public boolean verify(String inputCode) {
        if (Boolean.TRUE.equals(isVerified)) return true;
        if (inputCode == null || code == null) return false;
        if (isExpired()) return false;
        if (constantTimeEquals(code, inputCode)) {
            this.isVerified = true;
            return true;
        }
        return false;
    }

    /** 코드 재발급 + 만료 연장 */
    public void regenerateAndExtend(Duration ttl, int length) {
        this.code = generateCode(length);
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.expiresAt = now.plus(ttl);
        this.isVerified = false;
    }

    /* ====== 팩토리 ====== */

    /** 새 인증 DTO 생성 (기본 6자리 숫자 코드, TTL 분 단위) */
    public static MailAuthenticationDto newForMember(Long memberId, Duration ttl) {
        LocalDateTime now = LocalDateTime.now();
        return MailAuthenticationDto.builder()
                .memberId(memberId)
                .code(generateNumericCode(6))
                .isVerified(false)
                .createdAt(now)
                .expiresAt(now.plus(ttl))
                .build();
    }

    /** 엔티티 → DTO */
    public static MailAuthenticationDto fromEntity(MailAuthenticationEntity e) {
        if (e == null) return null;
        return MailAuthenticationDto.builder()
                .id(e.getId())
                .memberId(e.getMemberId() != null ? e.getMemberId()
                        : (e.getId() != null ? e.getId() : null))
                .code(e.getCode())
                .isVerified(Boolean.TRUE.equals(e.getIsVerified()))
                .createdAt(e.getCreatedAt())
                .expiresAt(e.getExpiresAt())
                .build();
    }

    /** DTO → 엔티티 (연관은 memberId만 설정, 실제 MemberEntity 주입은 서비스 레이어에서) */
    public MailAuthenticationEntity toEntity() {
        MailAuthenticationEntity entity = new MailAuthenticationEntity();
        entity.setId(this.id);
        entity.setMemberId(this.memberId);     // @JoinColumn과 중복 방지 위해 서비스에서 setMember(...)로 대체 가능
        entity.setCode(this.code);
        entity.setIsVerified(this.isVerified);
        entity.setCreatedAt(this.createdAt);
        entity.setExpiresAt(this.expiresAt);
        return entity;
    }

    /* ====== IMailAuthentication 구현부 ====== */

    @Override public void copyMailAuthentication(IMailAuthentication src) { IMailAuthentication.super.copyMailAuthentication(src); }
    @Override public void copyNotNullMailAuthentication(IMailAuthentication src) { IMailAuthentication.super.copyNotNullMailAuthentication(src); }

    /* ====== 내부 헬퍼 ====== */

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final char[] ALPHANUM = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789abcdefghijkmnopqrstuvwxyz".toCharArray();

    /** 영문+숫자 랜덤 코드 생성 */
    public static String generateCode(int length) {
        if (length <= 0) length = 16;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUM[SECURE_RANDOM.nextInt(ALPHANUM.length)]);
        }
        return sb.toString();
    }

    /** 숫자 6자리 등 간단 코드 */
    public static String generateNumericCode(int length) {
        if (length <= 0) length = 6;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) sb.append(SECURE_RANDOM.nextInt(10));
        return sb.toString();
    }

    /** 타이밍 공격 완화용 비교 */
    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        int result = a.length() ^ b.length();
        for (int i = 0; i < Math.min(a.length(), b.length()); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
