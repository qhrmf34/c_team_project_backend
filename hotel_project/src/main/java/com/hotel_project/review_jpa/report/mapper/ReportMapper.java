package com.hotel_project.review_jpa.report.mapper;

import com.hotel_project.review_jpa.report.dto.ReportDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReportMapper {

    //특정 리뷰의 신고 목록 조회
    List<ReportDto> findByReviewId(@Param("reviewId") Long reviewId);

    //중복 신고 체크
    boolean existsByMemberIdAndReviewId(
            @Param("memberId") Long memberId,
            @Param("reviewId") Long reviewId
    );
}