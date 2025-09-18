package com.hotel_project.member_jpa.member_image.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;

import java.time.LocalDateTime;

public interface IMemberImage extends IId {
    Long getId();
    void setId(Long id);

    IId getMember();
    void setMember(IId member);

    @JsonIgnore
    Long getMemberId();
    void setMemberId(Long memberId);

    ImageType getImageType();
    void setImageType(ImageType imageType);

    String getMemberImageName();
    void setMemberImageName(String memberImageName);

    String getMemberImagePath();
    void setMemberImagePath(String memberImagePath);

    Long getMemberImageSize();
    void setMemberImageSize(Long memberImageSize);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createAt);

    default void copyMember(IMemberImage iMemberImage) {
        if (iMemberImage == null) {
            return;
        }
        setId(iMemberImage.getId());
        setMember(iMemberImage.getMember());
        setImageType(iMemberImage.getImageType());
        setMemberImageName(iMemberImage.getMemberImageName());
        setMemberImagePath(iMemberImage.getMemberImagePath());
        setMemberImageSize(iMemberImage.getMemberImageSize());
        setCreatedAt(iMemberImage.getCreatedAt());
    }

    default void copyNotNullMember(IMemberImage iMemberImage) {
        if (iMemberImage == null) {
            return;
        }
        if (iMemberImage.getId() != null) setId(iMemberImage.getId());
        if (iMemberImage.getMember() != null) setMember(iMemberImage.getMember());
        if (iMemberImage.getImageType() != null) setImageType(iMemberImage.getImageType());
        if (iMemberImage.getMemberImageName() != null) setMemberImageName(iMemberImage.getMemberImageName());
        if (iMemberImage.getMemberImagePath() != null) setMemberImagePath(iMemberImage.getMemberImagePath());
        if (iMemberImage.getMemberImageSize() != null) setMemberImageSize(iMemberImage.getMemberImageSize());
    }
}
