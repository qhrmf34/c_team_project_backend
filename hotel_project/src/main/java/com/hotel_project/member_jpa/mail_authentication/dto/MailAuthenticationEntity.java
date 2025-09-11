package com.hotel_project.member_jpa.mail_authentication.dto;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mail_authentication_tbl")
public class MailAuthenticationEntity implements IMailAuthentication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // PK 자동증감

    @Column(name = "member_id", nullable = false)
    private Long memberId;   // 사용자 외래키

    @Column(columnDefinition = "TEXT")
    private String code;   // 인증 코드

    @Column(name = "is_verified", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isVerified = false;   // (1 인증 완료, 0 인증 불가)

    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}