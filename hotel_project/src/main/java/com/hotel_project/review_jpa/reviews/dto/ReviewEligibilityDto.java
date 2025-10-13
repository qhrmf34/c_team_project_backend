package com.hotel_project.review_jpa.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

// ReviewEligibilityDto.java (Response - 작성 가능 여부만)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEligibilityDto {
    private Boolean canWrite;
    private String status;
    private String message;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String paymentStatus;
    private Long reservationId;
    private Long existingReviewId;  // 이미 작성한 리뷰 ID (수정용)
}
