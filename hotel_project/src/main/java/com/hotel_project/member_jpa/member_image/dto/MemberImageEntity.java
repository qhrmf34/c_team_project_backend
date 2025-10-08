package com.hotel_project.member_jpa.member_image.dto;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.member_jpa.member.dto.MemberEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member_image_tbl")
public class MemberImageEntity implements IMemberImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity memberEntity;

    @Transient
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImageType imageType;

    @Column(nullable = false,length = 255)
    private String memberImageName;

    @Column(nullable = false, length = 500)
    private String memberImagePath;

    private Long memberImageSize;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Override
    public IId getMember(){
        return this.memberEntity;
    }

    @Override
    public void setMember(IId iId) {
        if (iId == null){
            return;
        }
        if (this.memberEntity == null){
            this.memberEntity = new MemberEntity();
        }
        this.memberEntity.copyMembersId(iId);
    }

    @Override
    public Long getMemberId() {
        return this.memberEntity != null ? this.memberEntity.getId() : null;
    }

    @Override
    public void setMemberId(Long memberId) {
        if (memberId == null){
            throw new IllegalArgumentException("memberId cannot be null");
        }
        if (this.memberEntity == null){
            this.memberEntity = new MemberEntity();
        }
        this.memberEntity.setId(memberId);
        this.memberId = memberId;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
    }
}
