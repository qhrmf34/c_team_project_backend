package com.hotel_project.review_jpa.report.repository;

import com.hotel_project.review_jpa.report.dto.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    // 중복 신고 방지 (같은 회원이 같은 리뷰를 중복 신고하지 못하도록)
    boolean existsByMemberEntity_IdAndReviewsEntity_Id(Long memberId, Long reviewsId);
}