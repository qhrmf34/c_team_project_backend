package com.hotel_project.payment_jpa.member_coupon.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.member_jpa.member.dto.MemberDto;
import com.hotel_project.payment_jpa.coupon.dto.CouponDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCouponDto implements IMemberCoupon{
    private Long id;

    @JsonIgnore
    private MemberDto memberDto;

    @NotNull
    private Long memberId;

    @JsonIgnore
    private CouponDto couponDto;

    @NotNull
    private Long couponId;

    @NotNull(message = "쿠폰 사용 여부은 필수 입력입니다.")
    private Boolean isUsed;

    private LocalDateTime usedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public IId getMember() { return this.memberDto; }

    @Override
    public void setMember(IId iId){
        if(iId == null){
            return;
        }
        if (this.memberDto == null){
            this.memberDto = new MemberDto();
        }
        this.memberDto.copyMembersId(iId);
    }

    @Override
    public Long getMemberId(){
        if ( this.memberDto != null){
            return this.memberDto.getId();
        }
        return this.memberId;
    }

    @Override
    public void setMemberId(Long memberId) {
        if(memberId == null){
            if (this.memberDto != null && this.memberDto.getId() != null){
                this.memberDto.setId(this.memberDto.getId());
            }
            return;
        }
        this.memberId = memberId;
        if (this.memberDto == null){
            this.memberDto = new MemberDto();
        }
        this.memberDto.setId(memberId);
    }

    @Override
    public IId getCoupon(){ return this.couponDto; }

    @Override
    public void setCoupon(IId iId) {
        if (iId == null){
            return;
        }
        if (this.couponDto == null){
            this.couponDto = new CouponDto();
        }
        this.couponDto.copyMembersId(iId);
    }

    @Override
    public Long getCouponId(){
        if ( this.couponDto != null){
            return this.couponDto.getId();
        }
        return this.couponId;
    }

    @Override
    public void setCouponId(Long couponId) {
        if (couponId == null){
            if (this.couponDto != null && this.couponDto.getId() != null){
                this.couponDto.setId(this.couponDto.getId());
            }
            return;
        }
        this.couponId = couponId;
        if (this.couponDto == null){
            this.couponDto = new CouponDto();
        }
        this.couponDto.setId(couponId);
    }
}

