package com.hotel_project.review_jpa.reviews.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.review_jpa.reviews.dto.ReviewCard;
import com.hotel_project.review_jpa.reviews.dto.ReviewsDto;
import com.hotel_project.review_jpa.reviews.dto.ReviewsEntity;
import com.hotel_project.review_jpa.reviews.mapper.ReviewsMapper;
import com.hotel_project.review_jpa.reviews.repository.ReviewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewsService {

    private final ReviewsRepository reviewsRepository;
    private final ReviewsMapper reviewsMapper;

    // ========== READ (MyBatis Mapper 사용) ==========

    public List<ReviewsDto> getReviewsByHotelId(Long hotelId) {
        log.info("호텔 ID {}의 리뷰 조회", hotelId);
        return reviewsMapper.findByHotelId(hotelId);
    }

    public List<ReviewsDto> getFilteredReviews(Long hotelId, String sortBy, ReviewCard reviewCard) {
        log.info("필터링된 리뷰 조회 - hotelId: {}, sortBy: {}, reviewCard: {}",
                hotelId, sortBy, reviewCard);

        return reviewsMapper.findFilteredReviews(hotelId, sortBy, reviewCard);
    }

    public Map<String, Long> getReviewCardStats(Long hotelId) {
        log.info("호텔 ID {}의 리뷰 카드 통계 조회", hotelId);
        return reviewsMapper.getReviewCardStats(hotelId);
    }

    public Map<String, Object> getHotelRatingStats(Long hotelId) {
        log.info("호텔 ID {}의 평점 통계 조회", hotelId);
        return reviewsMapper.getHotelRatingStats(hotelId);
    }

    public boolean hasReviewed(Long memberId, Long hotelId) {
        return reviewsMapper.existsByMemberIdAndHotelId(memberId, hotelId);
    }

    // ========== CREATE (JPA 사용) ==========

    @Transactional
    public ReviewsDto createReview(ReviewsDto reviewsDto) throws CommonExceptionTemplate {
        log.info("리뷰 작성 - memberId: {}, hotelId: {}",
                reviewsDto.getMemberId(), reviewsDto.getHotelId());

        // 중복 리뷰 체크
        if (reviewsRepository.existsByReservationsEntity_Id(reviewsDto.getReservationsId())) {
            throw new CommonExceptionTemplate(400, "이미 해당 예약에 대한 리뷰를 작성하셨습니다.");
        }

        ReviewsEntity entity = new ReviewsEntity();
        entity.setMemberId(reviewsDto.getMemberId());
        entity.setHotelId(reviewsDto.getHotelId());
        entity.setReservationsId(reviewsDto.getReservationsId());
        entity.setRating(reviewsDto.getRating());
        entity.setReviewContent(reviewsDto.getReviewContent());
        entity.setReviewCard(reviewsDto.getReviewCard());

        ReviewsEntity saved = reviewsRepository.save(entity);
        log.info("리뷰 작성 완료 - reviewId: {}", saved.getId());

        return convertToDto(saved);
    }

    // ========== UPDATE (JPA 사용) ==========

    @Transactional
    public ReviewsDto updateReview(Long reviewId, ReviewsDto reviewsDto, Long memberId) throws CommonExceptionTemplate {
        log.info("리뷰 수정 - reviewId: {}, memberId: {}", reviewId, memberId);

        ReviewsEntity entity = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "리뷰를 찾을 수 없습니다."));

        // 본인 확인
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

    // ========== DELETE (JPA 사용) ==========

    @Transactional
    public void deleteReview(Long reviewId, Long memberId) throws CommonExceptionTemplate {
        log.info("리뷰 삭제 - reviewId: {}, memberId: {}", reviewId, memberId);

        ReviewsEntity entity = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "리뷰를 찾을 수 없습니다."));

        // 본인 확인
        if (!entity.getMemberId().equals(memberId)) {
            throw new CommonExceptionTemplate(403, "본인의 리뷰만 삭제할 수 있습니다.");
        }

        reviewsRepository.delete(entity);
        log.info("리뷰 삭제 완료 - reviewId: {}", reviewId);
    }

    // ========== Helper Methods ==========

    private ReviewsDto convertToDto(ReviewsEntity entity) {
        ReviewsDto dto = new ReviewsDto();
        dto.copyMembers(entity);
        return dto;
    }
}