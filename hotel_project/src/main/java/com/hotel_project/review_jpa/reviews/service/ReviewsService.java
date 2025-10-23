package com.hotel_project.review_jpa.reviews.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.member_jpa.member.service.MemberNameFormatter;
import com.hotel_project.review_jpa.report.repository.ReportRepository;
import com.hotel_project.review_jpa.reviews.dto.*;
import com.hotel_project.review_jpa.reviews.mapper.ReviewsMapper;
import com.hotel_project.review_jpa.reviews.repository.ReviewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewsService {

    private final ReviewsRepository reviewsRepository;
    private final ReviewsMapper reviewsMapper;
    private final MemberNameFormatter memberNameFormatter;
    private final ReportRepository reportRepository;

    public List<ReviewsDto> getReviewsByHotelId(Long hotelId) {
        log.info("호텔 ID {}의 리뷰 조회", hotelId);
        List<ReviewsDto> reviews = reviewsMapper.findByHotelId(hotelId);

        // 각 리뷰의 memberName을 Service에서 포맷팅
        reviews.forEach(review -> {
            String displayName = memberNameFormatter.formatDisplayName(
                    review.getProvider(),
                    review.getFirstName(),
                    review.getLastName(),
                    review.getEmail()
            );
            review.setMemberName(displayName);
        });

        return reviews;
    }

    public List<ReviewsDto> getFilteredReviews(Long hotelId, String sortBy, ReviewCard reviewCard) {
        log.info("필터링된 리뷰 조회 - hotelId: {}, sortBy: {}, reviewCard: {}",
                hotelId, sortBy, reviewCard);
        List<ReviewsDto> reviews = reviewsMapper.findFilteredReviews(hotelId, sortBy, reviewCard);

        // 각 리뷰의 memberName을 Service에서 포맷팅
        reviews.forEach(review -> {
            String displayName = memberNameFormatter.formatDisplayName(
                    review.getProvider(),
                    review.getFirstName(),
                    review.getLastName(),
                    review.getEmail()
            );
            review.setMemberName(displayName);
        });

        return reviews;
    }

    public Map<String, Long> getReviewCardStats(Long hotelId) {
        log.info("호텔 ID {}의 리뷰 카드 통계 조회", hotelId);

        List<Map<String, Object>> rawList = reviewsMapper.getReviewCardStatsRaw(hotelId);

        Map<String, Long> result = new HashMap<>();

        result.put("NearPark", 0L);
        result.put("NearNightLife", 0L);
        result.put("NearTheater", 0L);
        result.put("CleanHotel", 0L);

        for (Map<String, Object> row : rawList) {
            String reviewCard = (String) row.get("reviewCard");
            Long count = ((Number) row.get("count")).longValue();
            result.put(reviewCard, count);
        }

        log.info("리뷰 카드 통계 결과: {}", result);
        return result;
    }

    public Map<String, Object> getHotelRatingStats(Long hotelId) {
        log.info("호텔 ID {}의 평점 통계 조회", hotelId);
        return reviewsMapper.getHotelRatingStats(hotelId);
    }

    public boolean hasReviewed(Long memberId, Long hotelId) {
        return reviewsMapper.existsByMemberIdAndHotelId(memberId, hotelId);
    }

    @Transactional
    public ReviewsDto createReview(ReviewsDto reviewsDto) throws CommonExceptionTemplate {
        log.info("리뷰 작성 - memberId: {}, hotelId: {}",
                reviewsDto.getMemberId(), reviewsDto.getHotelId());

        // 1. 이미 이 호텔에 리뷰를 작성했는지 체크
        if (hasReviewed(reviewsDto.getMemberId(), reviewsDto.getHotelId())) {
            throw new CommonExceptionTemplate(400, "이미 해당 호텔에 대한 리뷰를 작성하셨습니다.");
        }

        // 2. 리뷰 작성 가능 여부 체크
        ReviewEligibilityDto eligibility = checkReviewEligibility(
                reviewsDto.getHotelId(),
                reviewsDto.getMemberId()
        );

        if (!"ELIGIBLE".equals(eligibility.getStatus())) {
            String message = switch (eligibility.getStatus()) {
                case "NO_BOOKING" -> "해당 호텔에 예약 내역이 없습니다.";
                case "NOT_ELIGIBLE" -> "체크아웃 날짜가 지나고 결제가 완료된 후에 리뷰를 작성할 수 있습니다.";
                case "ALREADY_WRITTEN" -> "이미 해당 호텔에 대한 리뷰를 작성하셨습니다.";
                default -> "리뷰를 작성할 수 없습니다.";
            };
            throw new CommonExceptionTemplate(400, message);
        }

        // 3. 리뷰 작성 (reservationId는 eligibility에서 가져온 것 사용)
        ReviewsEntity entity = new ReviewsEntity();
        entity.setMemberId(reviewsDto.getMemberId());
        entity.setHotelId(reviewsDto.getHotelId());
        entity.setReservationsId(eligibility.getReservationId()); // 자동으로 설정
        entity.setRating(reviewsDto.getRating());
        entity.setReviewContent(reviewsDto.getReviewContent());
        entity.setReviewCard(reviewsDto.getReviewCard());

        ReviewsEntity saved = reviewsRepository.save(entity);
        log.info("리뷰 작성 완료 - reviewId: {}", saved.getId());

        return convertToDto(saved);
    }

    @Transactional
    public ReviewsDto updateReview(Long reviewId, ReviewsDto reviewsDto, Long memberId) throws CommonExceptionTemplate {
        log.info("리뷰 수정 - reviewId: {}, memberId: {}", reviewId, memberId);

        ReviewsEntity entity = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "리뷰를 찾을 수 없습니다."));

        if (!entity.getMemberId().equals(memberId)) {
            throw new CommonExceptionTemplate(403, "본인의 리뷰만 수정할 수 있습니다.");
        }

        entity.setRating(reviewsDto.getRating());
        entity.setReviewContent(reviewsDto.getReviewContent());
        entity.setReviewCard(reviewsDto.getReviewCard());

        ReviewsEntity updated = reviewsRepository.save(entity);
        log.info("리뷰 수정 완료 - reviewId: {}", reviewId);

        return convertToDto(updated);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long memberId) throws CommonExceptionTemplate {
        log.info("리뷰 삭제 - reviewId: {}, memberId: {}", reviewId, memberId);

        ReviewsEntity entity = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "리뷰를 찾을 수 없습니다."));

        if (!entity.getMemberId().equals(memberId)) {
            throw new CommonExceptionTemplate(403, "본인의 리뷰만 삭제할 수 있습니다.");
        }

        reportRepository.deleteByReviewsEntity_Id(reviewId);
        log.info("리뷰 관련 신고 삭제 완료 - reviewId: {}", reviewId);

        reviewsRepository.delete(entity);
        log.info("리뷰 삭제 완료 - reviewId: {}", reviewId);
    }

    public ReviewEligibilityDto checkReviewEligibility(Long hotelId, Long memberId) {
        try {
            Map<String, Object> result = reviewsMapper.checkReviewEligibility(hotelId, memberId);

            if (result == null || result.isEmpty()) {
                return ReviewEligibilityDto.builder()
                        .status("NO_BOOKING")
                        .canWrite(false)
                        .build();
            }

            String status = (String) result.get("status");
            Object checkInObj = result.get("checkIn");
            Object checkOutObj = result.get("checkOut");
            Object reservationIdObj = result.get("reservationId");

            LocalDate checkIn = convertToLocalDate(checkInObj);
            LocalDate checkOut = convertToLocalDate(checkOutObj);
            Long reservationId = convertToLong(reservationIdObj);

            boolean canWrite = "ELIGIBLE".equals(status);

            return ReviewEligibilityDto.builder()
                    .status(status)
                    .canWrite(canWrite)
                    .checkIn(checkIn)
                    .checkOut(checkOut)
                    .reservationId(reservationId)
                    .build();

        } catch (Exception e) {
            log.error("Error checking review eligibility", e);
            return ReviewEligibilityDto.builder()
                    .status("ERROR")
                    .canWrite(false)
                    .build();
        }
    }

    private LocalDate convertToLocalDate(Object value) {
        if (value == null) return null;
        if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate();
        }
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        }
        return null;
    }

    private Long convertToLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return null;
    }

    public ReviewsDto getMyReview(Long hotelId, Long memberId) {
        log.info("내 리뷰 조회 - hotelId: {}, memberId: {}", hotelId, memberId);
        ReviewsDto review = reviewsMapper.findMyReview(hotelId, memberId);

        if (review != null) {
            // memberName 포맷팅
            String displayName = memberNameFormatter.formatDisplayName(
                    review.getProvider(),
                    review.getFirstName(),
                    review.getLastName(),
                    review.getEmail()
            );
            review.setMemberName(displayName);
        }

        return review;
    }

    private ReviewsDto convertToDto(ReviewsEntity entity) {
        ReviewsDto dto = new ReviewsDto();
        dto.copyMembers(entity);
        return dto;
    }
}