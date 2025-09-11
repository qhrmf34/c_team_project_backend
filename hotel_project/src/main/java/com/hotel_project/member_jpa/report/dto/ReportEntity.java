package com.hotel_project.member_jpa.report.dto;


import com.hotel_project.member_jpa.member.dto.MemberEntity;
import com.hotel_project.review_jpa.choice.dto.ChoiceEntity; //빨강불 안뜨게 리뷰엔티티에서 임시로 바꿔두었음. 추후 수정필
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "report_tbl")
public class ReportEntity implements IReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // PK 자동 증가

    /** FK 원시값 (읽기 전용) */
    @Column(name = "review_id", nullable = false, insertable = false, updatable = false)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "review_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_report_review")
    )
    private ChoiceEntity review; //빨강불 안뜨게 리뷰엔티티에서 임시로 바꿔두었음. 추후 수정필

    /** FK 원시값 (읽기 전용) */
    @Column(name = "member_id", nullable = false, insertable = false, updatable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_report_member")
    )
    private MemberEntity member;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 50)
    private ReportType reportType;

    @Column(name = "report_content", columnDefinition = "TEXT")
    private String reportContent;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /* ===== IReport 인터페이스 호환 ===== */

    @Override
    public String getReportType() {
        return (reportType != null ? reportType.name() : null);
    }

    @Override
    public void setReportType(String reportType) {
        this.reportType = (reportType != null ? ReportType.valueOf(reportType) : null);
    }

    @Override
    public Long getReviewId() {
        if (reviewId != null) return reviewId;
        return (review != null ? review.getId() : null);
    }

    @Override
    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    @Override
    public Long getMemberId() {
        if (memberId != null) return memberId;
        return (member != null ? member.getId() : null);
    }

    @Override
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}

