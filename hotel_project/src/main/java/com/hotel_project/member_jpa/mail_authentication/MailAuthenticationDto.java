package com.hotel_project.member_jpa.mail_authentication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.member_jpa.member.dto.MemberDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailAuthenticationDto implements IMailAuthentication {
    private Long id;

    @JsonIgnore
    private MemberDto memberDto;

    @NotNull
    private Long memberId;

    private String code;

    @NotNull
    private Boolean isVerified;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @Override
    public IId getMember() {
        return this.memberDto;
    }

    @Override
    public void setMember(IId iId) {
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
}
