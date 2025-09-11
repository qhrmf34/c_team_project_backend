package com.hotel_project.member_jpa.member_image.dto;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.hotel_project.member_jpa.member.dto.MemberEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member_image_tbl")
public class MemberImageEntity implements IMemberImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // PK 자동 증가

    /** FK 원시값 (읽기 전용, IMemberImage 인터페이스 호환용) */
    @Column(name = "member_id", nullable = false, insertable = false, updatable = false)
    private Long memberId;

    /** MemberEntity와 다대일 관계 */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_member_image_member")
    )
    private MemberEntity member;

    @Column(name = "member_image_name", nullable = false, length = 255)
    private String memberImageName;

    @Column(name = "member_image_path", nullable = false, length = 500)
    private String memberImagePath;

    @Column(name = "member_image_size")
    private Long memberImageSize;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /* ===== IMemberImage 인터페이스 호환 ===== */

    @Override
    public Long getMemberId() {
        if (memberId != null) return memberId;
        return (member != null ? member.getId() : null);
    }

    @Override
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
        // 실제 MemberEntity는 서비스 계층에서 setMember(...)로 주입하는 것을 권장
    }
}
