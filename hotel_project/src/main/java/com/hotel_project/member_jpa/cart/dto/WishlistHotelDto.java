package com.hotel_project.member_jpa.cart.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 찜 목록 조회용 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistHotelDto {
    // Cart 정보
    private Long cartId;
    private Long memberId;
    private Long hotelId;
    private LocalDateTime createdAt;

    // Hotel 기본 정보
    private String hotelName;
    private String hotelType;
    private Integer hotelStar;

    // 추가 정보
    private String hotelImage;        // 첫 번째 이미지
    private Integer imageCount;       // 이미지 개수
    private Long minPrice;           // 최저 가격
    private String cityName;         // 도시명
    private String countryName;      // 국가명
    private Integer amenitiesCount;  // 편의시설 개수
    private Double hotelRating;      // 평균 평점
    private Integer reviewCount;     // 리뷰 개수
}