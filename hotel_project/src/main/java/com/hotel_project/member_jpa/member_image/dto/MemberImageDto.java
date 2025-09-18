package com.hotel_project.member_jpa.member_image.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.member_jpa.member.dto.MemberDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberImageDto implements IMemberImage {
    private Long id;

    @JsonIgnore
    private MemberDto memberDto;

    @NotNull
    private Long memberId;

    @NotNull(message = "배경, 사용자 구분은 필수 입력 입니다.")
    private ImageType imageType;

    @NotBlank(message = "사용자 이미지 이름은 필수 입력 입니다.")
    @Size(max = 255, message = "사용자 이미지 이름은 255자 이하로 입력해야 합니다.")
    private String memberImageName;

    @NotBlank(message = "사용자 이미지 경로는 필수 입력 입니다.")
    @Size(max = 500, message = "사용자 이미지 경로는 500자 이하로 입력해야 합니다.")
    private String memberImagePath;

    private Long memberImageSize;

    private LocalDateTime createdAt;

    @Override
    public IId getMember(){
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
    public Long getMemberId(){
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
