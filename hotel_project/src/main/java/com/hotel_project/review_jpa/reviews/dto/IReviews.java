package com.hotel_project.review_jpa.reviews.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface IReviews extends IId {
    Long getId();
    void setId(Long id);

    Long getMemberId();
    void setMemberId(Long memberId);

    @JsonIgnore
    IId getMember();
    void setMember(IId member);

    Long getHotelId();
    void setHotelId(Long hotelId);

    @JsonIgnore
    IId getHotel();
    void setHotel(IId hotel);

    Long getReservationsId();
    void setReservationsId(Long reservationsId);

    @JsonIgnore
    IId getReservations();
    void setReservations(IId reservations);

    BigDecimal getRating();
    void setRating(BigDecimal rating);

    ReviewCard getReviewCard();
    void setReviewCard(ReviewCard reviewCard);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    default void copyMembers(IReviews iReviews) {
        if (iReviews == null) {
            return;
        }
        setId(iReviews.getId());
        setMember(iReviews.getMember());
        setHotel(iReviews.getHotel());
        setReservations(iReviews.getReservations());
        setRating(iReviews.getRating());
        setReviewCard(iReviews.getReviewCard());
        setCreatedAt(iReviews.getCreatedAt());
        setUpdatedAt(iReviews.getUpdatedAt());
    }

    default void copyNotNullMembers(IReviews iReviews) {
        if (iReviews == null) {
            return;
        }
        if (iReviews.getId() != null) {
            setId(iReviews.getId());
        }
        if (iReviews.getMember() != null) {
            setMember(iReviews.getMember());
        }
        if (iReviews.getHotel() != null) {
            setHotel(iReviews.getHotel());
        }
        if (iReviews.getReservations() != null) {
            setReservations(iReviews.getReservations());
        }
        if (iReviews.getRating() != null) {
            setRating(iReviews.getRating());
        }
        if (iReviews.getReviewCard() != null) {
            setReviewCard(iReviews.getReviewCard());
        }
        if (iReviews.getUpdatedAt() != null) {
            setUpdatedAt(iReviews.getUpdatedAt());
        }
    }
}
