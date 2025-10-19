package com.hotel_project.review_jpa.reviews.repository;

import com.hotel_project.review_jpa.reviews.dto.ReviewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewsRepository extends JpaRepository<ReviewsEntity, Long> {

    // 예약 ID로 리뷰 존재 여부 확인 (중복 리뷰 방지)
    boolean existsByReservationsEntity_Id(Long reservationsId);

    // 회원 ID와 호텔 ID로 리뷰 존재 여부 확인
    boolean existsByMemberEntity_IdAndHotelEntity_Id(Long memberId, Long hotelId);

}