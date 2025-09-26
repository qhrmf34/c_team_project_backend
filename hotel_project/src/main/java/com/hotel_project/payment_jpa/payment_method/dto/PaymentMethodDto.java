package com.hotel_project.payment_jpa.payment_method.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.member_jpa.member.dto.MemberDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodDto implements IPaymentMethod {
    private Long id;

    @JsonIgnore
    private MemberDto memberDto;

    @NotNull
    private Long memberId;

    @NotNull
    @Size(max = 50)
    private String tossKey;

    private String cardLastFour;      // 카드번호 마지막 4자리

    private String cardCompany;       // 카드사 (KB, 삼성, 현대 등)

    private String cardType;          // 카드 타입 (VISA, MasterCard 등)

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public IId getMember() { return this.memberDto; }

    @Override
    public void setMember(IId iId){
        if ( iId == null){
            return;
        }
        if (this.memberDto == null){
            this.memberDto = new MemberDto();
        }
        this.memberDto.copyMembersId(iId);
    }

    @Override
    public Long getMemberId() {
        if ( this.memberDto != null){
            return this.memberDto.getId();
        }
        return this.memberId;
    }

    @Override
    public void setMemberId(Long memberId) {
        if ( memberId == null){
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
}
