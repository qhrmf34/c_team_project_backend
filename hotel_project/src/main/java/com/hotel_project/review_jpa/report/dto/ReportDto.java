package com.hotel_project.review_jpa.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.member_jpa.member.dto.MemberDto;
import com.hotel_project.review_jpa.reviews.dto.ReviewsDto;
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

    @JsonIgnore
    private ReviewsDto reviewsDto;

    @NotNull
    private Long reviewsId;

    @JsonIgnore
    private MemberDto memberDto;

    @NotNull
    private Long memberId;

    @NotNull
    private ReportType reportType;

    private String reportContent;

    private LocalDateTime createdAt;

    @Override
    public IId getReviews() {
        return this.reviewsDto;
    }

    @Override
    public void setReviews(IId iId){
        if (iId == null){
            return;
        }
        if (this.reviewsDto == null){
            this.reviewsDto = new ReviewsDto();
        }
        this.reviewsDto.copyMembersId(iId);
    }

    @Override
    public Long getReviewsId() {
        if (this.reviewsDto != null){
            return this.reviewsDto.getId();
        }
        return this.reviewsId;
    }

    @Override
    public void setReviewsId(Long id) {
        if (id == null){
            if (this.reviewsDto != null && this.reviewsDto.getId() != null){
                this.reviewsDto.setId(this.reviewsDto.getId());
            }
            return;
        }
        this.reviewsId = id;
        if (this.reviewsDto == null){
            this.reviewsDto = new ReviewsDto();
        }
        this.reviewsDto.setId(id);
    }

    @Override
    public IId getMember() {
        return this.memberDto;
    }

    @Override
    public void setMember(IId iId){
        if (iId == null){
            return;
        }
        if (this.memberDto == null){
            this.memberDto = new MemberDto();
        }
        this.memberDto.copyMembersId(iId);
    }

    @Override
    public Long getMemberId() {
        if (this.memberDto != null){
            return this.memberDto.getId();
        }
        return this.memberId;
    }

    @Override
    public void setMemberId(Long id) {
        if (id == null){
            if (this.memberDto != null && this.memberDto.getId() != null){
                this.memberDto.setId(this.memberDto.getId());
            }
            return;
        }
        this.memberId = id;
        if (this.memberDto == null){
            this.memberDto = new MemberDto();
        }
        this.memberDto.setId(id);
    }
}
