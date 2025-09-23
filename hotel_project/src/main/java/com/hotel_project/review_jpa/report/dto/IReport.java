package com.hotel_project.review_jpa.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.hotel.dto.IHotel;

import java.time.LocalDateTime;

public interface IReport extends IId {
    Long getId();
    void setId(Long id);

    @JsonIgnore
    IId getReviews();
    void setReviews(IId reviews);

    Long getReviewsId();
    void setReviewsId(Long reviewsId);

    @JsonIgnore
    IId getMember();
    void setMember(IId member);

    Long getMemberId();
    void setMemberId(Long memberId);

    ReportType getReportType();
    void setReportType(ReportType report);

    String getReportContent();
    void setReportContent(String reportContent);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    default void copyMembers(IReport iReport) {
        if (iReport == null) {
            return;
        }
        setId(iReport.getId());
        setReviews(iReport.getReviews());
        setMember(iReport.getMember());
        setReportType(iReport.getReportType());
        setReportContent(iReport.getReportContent());
        setCreatedAt(iReport.getCreatedAt());
    }

    default void copyNotNullMembers(IReport iReport) {
        if (iReport == null) {
            return;
        }
        if (iReport.getId() != null) {
            setId(iReport.getId());
        }
        if (iReport.getReviews() != null) {setReviews(iReport.getReviews());}
        if (iReport.getMember() != null) {setMember(iReport.getMember());}
        if (iReport.getReportType() != null) {setReportType(iReport.getReportType());}
        if (iReport.getReportContent() != null) {setReportContent(iReport.getReportContent());}
    }
}
