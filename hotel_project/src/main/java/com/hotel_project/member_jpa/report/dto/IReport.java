package com.hotel_project.member_jpa.report.dto;

import java.time.LocalDateTime;

public interface IReport {
    Long getId();
    void setId(Long id);

    Long getReviewId();
    void setReviewId(Long reviewId);

    Long getMemberId();
    void setMemberId(Long memberId);

    /** DB: ENUM (스팸/광고, 부적절한 내용 등) - 값 확정 전까지 String 사용 */
    String getReportType();
    void setReportType(String reportType);

    String getReportContent();
    void setReportContent(String reportContent);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    /* insert용: 모든 값 복사 */
    default void copyReport(IReport src) {
        setId(src.getId());
        setReviewId(src.getReviewId());
        setMemberId(src.getMemberId());
        setReportType(src.getReportType());
        setReportContent(src.getReportContent());
        setCreatedAt(src.getCreatedAt());
    }

    /* update용: null 아닌 값만 복사 */
    default void copyNotNullReport(IReport src) {
        if (src.getId() != null) setId(src.getId());
        if (src.getReviewId() != null) setReviewId(src.getReviewId());
        if (src.getMemberId() != null) setMemberId(src.getMemberId());
        if (src.getReportType() != null) setReportType(src.getReportType());
        if (src.getReportContent() != null) setReportContent(src.getReportContent());
        if (src.getCreatedAt() != null) setCreatedAt(src.getCreatedAt());
    }
}

