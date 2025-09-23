package com.hotel_project.review_jpa.report.dto;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.member_jpa.member.dto.MemberEntity;
import com.hotel_project.review_jpa.reviews.dto.ReviewsEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "report_tbl")
public class ReportEntity implements IReport{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviews_id", nullable = false)
    private ReviewsEntity reviewsEntity;

    @Transient
    private Long reviewsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;

    @Transient
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType reportType;

    @Lob
    private String reportContent;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Override
    public IId getReviews(){
        return this.reviewsEntity;
    }

    @Override
    public void setReviews(IId iId) {
        if (iId == null){
            return;
        }
        if (this.reviewsEntity == null){
            this.reviewsEntity = new ReviewsEntity();
        }
        this.reviewsEntity.copyMembersId(iId);
    }

    @Override
    public Long getReviewsId() {
        return this.reviewsEntity != null ? this.reviewsEntity.getId() : null;
    }

    @Override
    public void setReviewsId(Long reviewsId) {
        if (reviewsId == null) {
            throw new IllegalArgumentException("reviewsId cannot be null");
        }
        if (this.reviewsEntity == null){
            this.reviewsEntity = new ReviewsEntity();
        }
        this.reviewsEntity.setId(reviewsId);
        this.reviewsId = reviewsId;
    }

    @Override
    public IId getMember(){
        return this.memberEntity;
    }

    @Override
    public void setMember(IId iId) {
        if (iId == null){
            return;
        }
        if (this.memberEntity == null){
            this.memberEntity = new MemberEntity();
        }
        this.memberEntity.copyMembersId(iId);
    }

    @Override
    public Long getMemberId() {
        return this.memberEntity != null ? this.memberEntity.getId() : null;
    }

    @Override
    public void setMemberId(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("memberId cannot be null");
        }
        if (this.memberEntity == null){
            this.memberEntity = new MemberEntity();
        }
        this.memberEntity.setId(memberId);
        this.memberId = memberId;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
    }
}
