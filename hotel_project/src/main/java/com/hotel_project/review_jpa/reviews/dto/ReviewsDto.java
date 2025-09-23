package com.hotel_project.review_jpa.reviews.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.hotel.dto.HotelDto;
import com.hotel_project.member_jpa.member.dto.MemberDto;
import com.hotel_project.payment_jpa.reservations.dto.ReservationsDto;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewsDto implements IReviews {
    private Long id;

    @JsonIgnore
    private MemberDto memberDto;

    @NotNull
    private Long memberId;

    @JsonIgnore
    private HotelDto hotelDto;

    @NotNull
    private Long hotelId;

    @JsonIgnore
    private ReservationsDto reservationsDto;

    @NotNull
    private Long reservationsId;

    @DecimalMax(value = "5.0", inclusive = true, message = "평점은 5.0 이하 이여야 합니다.")
    @DecimalMin(value = "0.0", inclusive = true, message = "평점은 0.0 이상 이여야 합니다.")
    @Digits(integer = 1, fraction = 1, message = "평점은 최대 1자리 정수, 1자리 소수까지 가능합니다.")
    private BigDecimal rating;

    private String reviewContent;

    @NotNull(message = "리뷰 카드를 선택 해주세요.")
    private ReviewCard reviewCard;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

    @Override
    public IId getHotel() {
        return this.hotelDto;
    }

    @Override
    public void setHotel(IId iId){
        if (iId == null){
            return;
        }
        if (this.hotelDto == null){
            this.hotelDto = new HotelDto();
        }
        this.hotelDto.copyMembersId(iId);
    }

    @Override
    public Long getHotelId() {
        if (this.hotelDto != null){
            return this.hotelDto.getId();
        }
        return this.hotelId;
    }

    @Override
    public void setHotelId(Long id) {
        if (id == null){
            if (this.hotelDto != null && this.hotelDto.getId() != null){
                this.hotelDto.setId(this.hotelDto.getId());
            }
            return;
        }
        this.hotelId = id;
        if (this.hotelDto == null){
            this.hotelDto = new HotelDto();
        }
        this.hotelDto.setId(id);
    }

    @Override
    public IId getReservations() {
        return this.reservationsDto;
    }

    @Override
    public void setReservations(IId iId){
        if (iId == null){
            return;
        }
        if (this.reservationsDto == null){
            this.reservationsDto = new ReservationsDto();
        }
        this.reservationsDto.copyMembersId(iId);
    }

    @Override
    public Long getReservationsId() {
        if (this.reservationsDto != null){
            return this.reservationsDto.getId();
        }
        return this.reservationsId;
    }

    @Override
    public void setReservationsId(Long id) {
        if (id == null){
            if (this.reservationsDto != null && this.reservationsDto.getId() != null){
                this.reservationsDto.setId(this.reservationsDto.getId());
            }
            return;
        }
        this.reservationsId = id;
        if (this.reservationsDto == null){
            this.reservationsDto = new ReservationsDto();
        }
        this.reservationsDto.setId(id);
    }
}
