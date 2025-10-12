package com.hotel_project.review_jpa.reviews.mapper;

import com.hotel_project.review_jpa.reviews.dto.ReviewCard;
import com.hotel_project.review_jpa.reviews.dto.ReviewsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReviewsMapper {
    // 특정 호텔의 모든 리뷰 조회
    List<ReviewsDto> findByHotelId(@Param("hotelId") Long hotelId);


    //필터링된 리뷰 조회 (평점순, 카드별)
    List<ReviewsDto> findFilteredReviews(
            @Param("hotelId") Long hotelId,
            @Param("sortBy") String sortBy,
            @Param("reviewCard") ReviewCard reviewCard
    );


    // ReviewCard별 리뷰 개수 통계
    // @return Map { reviewCard, count }
    Map<String, Long> getReviewCardStats(@Param("hotelId") Long hotelId);


    //호텔의 평균 평점과 리뷰 개수
    //@return Map { averageRating, reviewCount }
    Map<String, Object> getHotelRatingStats(@Param("hotelId") Long hotelId);


    //중복 리뷰 체크
    boolean existsByMemberIdAndHotelId(
            @Param("memberId") Long memberId,
            @Param("hotelId") Long hotelId
    );

    //특정 회원이 특정 호텔에 작성한 리뷰 조회
    ReviewsDto findMyReview(
            @Param("hotelId") Long hotelId,
            @Param("memberId") Long memberId
    );


    //리뷰 작성 가능 여부 체크
    //@return Map { status, checkIn, checkOut, paymentStatus, reservationId }
    Map<String, Object> checkReviewEligibility(
            @Param("hotelId") Long hotelId,
            @Param("memberId") Long memberId
    );


    //리뷰 등록
    int insertReview(ReviewsDto reviewsDto);


    //리뷰 수정
    int updateReview(ReviewsDto reviewsDto);


    //리뷰 삭제
    int deleteReview(
            @Param("id") Long id,
            @Param("memberId") Long memberId
    );


    //리뷰 작성자 확인
    boolean isReviewOwner(
            @Param("reviewId") Long reviewId,
            @Param("memberId") Long memberId
    );

    List<Map<String, Object>> getReviewCardStatsRaw(@Param("hotelId") Long hotelId);
}