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

    public List<ReportDto> getReportsByReviewId(Long reviewId) {
        log.info("리뷰 ID {}의 신고 목록 조회", reviewId);
        return reportMapper.findByReviewId(reviewId);
    }

    public boolean hasReported(Long memberId, Long reviewId) {
        return reportMapper.existsByMemberIdAndReviewId(memberId, reviewId);
    }

    @Transactional
    public ReportDto createReport(ReportDto reportDto) throws CommonExceptionTemplate {
        log.info("리뷰 신고 - memberId: {}, reviewId: {}, type: {}",
                reportDto.getMemberId(), reportDto.getReviewsId(), reportDto.getReportType());

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

    @Transactional
    public void deleteReport(Long reportId) throws CommonExceptionTemplate {
        log.info("신고 삭제 - reportId: {}", reportId);

        ReportEntity entity = reportRepository.findById(reportId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "신고를 찾을 수 없습니다."));

        reportRepository.delete(entity);
        log.info("신고 삭제 완료 - reportId: {}", reportId);
    }

    //본인 리뷰 신고 방지 체크
    // 리뷰 작성자 ID를 조회하여 본인 여부 확인
    @Transactional
    public ReportDto createReportWithValidation(ReportDto reportDto) throws CommonExceptionTemplate {
        log.info("리뷰 신고 (본인 체크 포함) - memberId: {}, reviewId: {}",
                reportDto.getMemberId(), reportDto.getReviewsId());

        // 1. 본인 리뷰 신고 방지
        Long reviewOwnerId = reportMapper.getReviewOwnerId(reportDto.getReviewsId());
        if (reviewOwnerId != null && reviewOwnerId.equals(reportDto.getMemberId())) {
            throw new CommonExceptionTemplate(400, "본인의 리뷰는 신고할 수 없습니다.");
        }

        // 2. 중복 신고 체크
        if (reportMapper.existsByMemberIdAndReviewId(
                reportDto.getMemberId(), reportDto.getReviewsId())) {
            throw new CommonExceptionTemplate(400, "이미 해당 리뷰를 신고하셨습니다.");
        }

        // 3. 신고 생성
        return createReport(reportDto);
    }

    // Helper Methods
    private ReportDto convertToDto(ReportEntity entity) {
        ReportDto dto = new ReportDto();
        dto.copyMembers(entity);
        return dto;
    }
}