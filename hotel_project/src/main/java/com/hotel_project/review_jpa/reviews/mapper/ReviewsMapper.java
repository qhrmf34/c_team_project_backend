package com.hotel_project.review_jpa.reviews.mapper;

import com.hotel_project.review_jpa.reviews.dto.ReviewCard;
import com.hotel_project.review_jpa.reviews.dto.ReviewsDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReviewsMapper {

    /**
     * 특정 호텔의 모든 리뷰 조회
     */
    List<ReviewsDto> findByHotelId(@Param("hotelId") Long hotelId);

    /**
     * 필터링된 리뷰 조회 (평점순, 카드별)
     */
    List<ReviewsDto> findFilteredReviews(
            @Param("hotelId") Long hotelId,
            @Param("sortBy") String sortBy,
            @Param("reviewCard") ReviewCard reviewCard
    );

    /**
     * ReviewCard별 리뷰 개수 통계
     */
    Map<String, Long> getReviewCardStats(@Param("hotelId") Long hotelId);

    /**
     * 호텔의 평균 평점과 리뷰 개수
     */
    Map<String, Object> getHotelRatingStats(@Param("hotelId") Long hotelId);

    /**
     * 중복 리뷰 체크
     */
    boolean existsByMemberIdAndHotelId(
            @Param("memberId") Long memberId,
            @Param("hotelId") Long hotelId
    );
}