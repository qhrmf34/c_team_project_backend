package com.hotel_project.member_jpa.report.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDto implements IReport {

    private Long id;

    @NotNull
    private Long reviewId;

    @NotNull
    private Long memberId;

    /** 신고 유형 (ENUM: SPAM, INAPPROPRIATE, HARASSMENT, OTHER) */
    private String reportType;

    private String reportContent;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /* ========== 변환 메서드 ========== */

    public static ReportDto fromEntity(ReportEntity e) {
        if (e == null) return null;
        return ReportDto.builder()
                .id(e.getId())
                .reviewId(e.getReviewId() != null ? e.getReviewId()
                        : (e.getReview() != null ? e.getReview().getId() : null))
                .memberId(e.getMemberId() != null ? e.getMemberId()
                        : (e.getMember() != null ? e.getMember().getId() : null))
                .reportType(e.getReportType())
                .reportContent(e.getReportContent())
                .createdAt(e.getCreatedAt())
                .build();
    }

    public ReportEntity toEntity() {
        ReportEntity e = new ReportEntity();
        e.setId(this.id);
        e.setReviewId(this.reviewId);  // 실제 저장 시에는 setReview(...)로 주입 권장
        e.setMemberId(this.memberId);  // 실제 저장 시에는 setMember(...)로 주입 권장
        e.setReportType(this.reportType);
        e.setReportContent(this.reportContent);
        e.setCreatedAt(this.createdAt);
        return e;
    }

    /* ========== IReport 기본 구현 위임 ========== */

    @Override public void copyReport(IReport src) { IReport.super.copyReport(src); }
    @Override public void copyNotNullReport(IReport src) { IReport.super.copyNotNullReport(src); }

    /* ========== 편의 메서드 ========== */

    /** 신고 등록용 팩토리 */
    public static ReportDto newReport(Long reviewId, Long memberId, String type, String content) {
        return ReportDto.builder()
                .reviewId(reviewId)
                .memberId(memberId)
                .reportType(type)
                .reportContent(content)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
