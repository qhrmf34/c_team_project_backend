package com.hotel_project.review_jpa.report.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.review_jpa.report.dto.ReportDto;
import com.hotel_project.review_jpa.report.dto.ReportEntity;
import com.hotel_project.review_jpa.report.mapper.ReportMapper;
import com.hotel_project.review_jpa.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    // ========== READ (MyBatis Mapper 사용) ==========

    public List<ReportDto> getReportsByReviewId(Long reviewId) {
        log.info("리뷰 ID {}의 신고 목록 조회", reviewId);
        return reportMapper.findByReviewId(reviewId);
    }

    public boolean hasReported(Long memberId, Long reviewId) {
        return reportMapper.existsByMemberIdAndReviewId(memberId, reviewId);
    }

    // ========== CREATE (JPA 사용) ==========

    @Transactional
    public ReportDto createReport(ReportDto reportDto) throws CommonExceptionTemplate {
        log.info("리뷰 신고 - memberId: {}, reviewId: {}, type: {}",
                reportDto.getMemberId(), reportDto.getReviewsId(), reportDto.getReportType());

        // 중복 신고 체크
        if (reportRepository.existsByMemberEntity_IdAndReviewsEntity_Id(
                reportDto.getMemberId(), reportDto.getReviewsId())) {
            throw new CommonExceptionTemplate(400, "이미 해당 리뷰를 신고하셨습니다.");
        }

        ReportEntity entity = new ReportEntity();
        entity.setMemberId(reportDto.getMemberId());
        entity.setReviewsId(reportDto.getReviewsId());
        entity.setReportType(reportDto.getReportType());
        entity.setReportContent(reportDto.getReportContent());

        ReportEntity saved = reportRepository.save(entity);
        log.info("리뷰 신고 완료 - reportId: {}", saved.getId());

        return convertToDto(saved);
    }

    // ========== DELETE (JPA 사용) ==========

    @Transactional
    public void deleteReport(Long reportId) throws CommonExceptionTemplate {
        log.info("신고 삭제 - reportId: {}", reportId);

        ReportEntity entity = reportRepository.findById(reportId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "신고를 찾을 수 없습니다."));

        reportRepository.delete(entity);
        log.info("신고 삭제 완료 - reportId: {}", reportId);
    }

    // ========== Helper Methods ==========

    private ReportDto convertToDto(ReportEntity entity) {
        ReportDto dto = new ReportDto();
        dto.copyMembers(entity);
        return dto;
    }
}