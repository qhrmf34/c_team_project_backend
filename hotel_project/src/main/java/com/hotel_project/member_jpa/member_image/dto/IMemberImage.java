package com.hotel_project.member_jpa.member_image.dto;

import java.time.LocalDateTime;

public interface IMemberImage {
    Long getId();
    void setId(Long id);

    Long getMemberId();
    void setMemberId(Long memberId);

    String getMemberImageName();
    void setMemberImageName(String memberImageName);

    String getMemberImagePath();
    void setMemberImagePath(String memberImagePath);

    Long getMemberImageSize();
    void setMemberImageSize(Long memberImageSize);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    /* insert용: 모든 값 복사 */
    default void copyMemberImage(IMemberImage src) {
        setId(src.getId());
        setMemberId(src.getMemberId());
        setMemberImageName(src.getMemberImageName());
        setMemberImagePath(src.getMemberImagePath());
        setMemberImageSize(src.getMemberImageSize());
        setCreatedAt(src.getCreatedAt());
    }

    /* update용: null 아닌 값만 복사 */
    default void copyNotNullMemberImage(IMemberImage src) {
        if (src.getId() != null) setId(src.getId());
        if (src.getMemberId() != null) setMemberId(src.getMemberId());
        if (src.getMemberImageName() != null) setMemberImageName(src.getMemberImageName());
        if (src.getMemberImagePath() != null) setMemberImagePath(src.getMemberImagePath());
        if (src.getMemberImageSize() != null) setMemberImageSize(src.getMemberImageSize());
        if (src.getCreatedAt() != null) setCreatedAt(src.getCreatedAt());
    }
}
