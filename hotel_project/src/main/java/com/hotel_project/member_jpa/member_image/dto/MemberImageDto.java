package com.hotel_project.member_jpa.member_image.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberImageDto implements IMemberImage {

    private Long id;

    @NotNull
    private Long memberId;

    @NotBlank
    @Size(max = 255)
    private String memberImageName;

    @NotBlank
    @Size(max = 500)
    private String memberImagePath;

    private Long memberImageSize; // bytes

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /* ======================== 이미지 변경 헬퍼 ======================== */

    /**
     * 프로필 이미지 교체 (이름/경로/사이즈 업데이트).
     * @param name  새 파일명 (예: avatar.png)
     * @param path  저장 경로 (예: /uploads/members/123/avatar.png)
     * @param size  파일 크기(bytes) - 모르면 null 허용
     * @param resetCreatedAt true면 업로드 시각을 현재로 갱신
     */
    public void changeImage(@NotBlank String name, @NotBlank String path, Long size, boolean resetCreatedAt) {
        this.memberImageName = name;
        this.memberImagePath = path;
        this.memberImageSize = size;
        if (resetCreatedAt || this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    /** 간단한 변경 단축 메서드 (createdAt 갱신 포함) */
    public void changeImage(@NotBlank String name, @NotBlank String path, Long size) {
        changeImage(name, path, size, true);
    }

    /* ======================== 변환 메서드 ======================== */

    public static MemberImageDto fromEntity(MemberImageEntity e) {
        if (e == null) return null;
        return MemberImageDto.builder()
                .id(e.getId())
                .memberId(e.getMemberId() != null ? e.getMemberId()
                        : (e.getMember() != null ? e.getMember().getId() : null))
                .memberImageName(e.getMemberImageName())
                .memberImagePath(e.getMemberImagePath())
                .memberImageSize(e.getMemberImageSize())
                .createdAt(e.getCreatedAt())
                .build();
    }

    /** DTO → 엔티티 (연관은 서비스 레이어에서 setMember(...)로 주입 권장) */
    public MemberImageEntity toEntity() {
        MemberImageEntity e = new MemberImageEntity();
        e.setId(this.id);
        e.setMemberId(this.memberId); // 저장 시 서비스에서 e.setMember(member)로 보완하세요.
        e.setMemberImageName(this.memberImageName);
        e.setMemberImagePath(this.memberImagePath);
        e.setMemberImageSize(this.memberImageSize);
        e.setCreatedAt(this.createdAt);
        return e;
    }

    /* ===================== IMemberImage 기본 구현 위임 ===================== */

    @Override public void copyMemberImage(IMemberImage src) { IMemberImage.super.copyMemberImage(src); }
    @Override public void copyNotNullMemberImage(IMemberImage src) { IMemberImage.super.copyNotNullMemberImage(src); }

    /* ======================== 편의 팩토리 ======================== */

    /** 새 이미지 DTO 생성(업로드 직후) */
    public static MemberImageDto newUpload(Long memberId, String name, String path, Long size) {
        LocalDateTime now = LocalDateTime.now();
        return MemberImageDto.builder()
                .memberId(memberId)
                .memberImageName(name)
                .memberImagePath(path)
                .memberImageSize(size)
                .createdAt(now)
                .build();
    }
}

