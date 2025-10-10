package com.hotel_project.member_jpa.member_image.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.member_jpa.member.dto.MemberEntity;
import com.hotel_project.member_jpa.member.repository.MemberRepository;
import com.hotel_project.member_jpa.member_image.dto.ImageType;
import com.hotel_project.member_jpa.member_image.dto.MemberImageDto;
import com.hotel_project.member_jpa.member_image.dto.MemberImageEntity;
import com.hotel_project.member_jpa.member_image.repository.MemberImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberImageService {

    private final MemberImageRepository memberImageRepository;
    private final MemberRepository memberRepository;

    private String getUploadPath() {
        String projectRoot = System.getProperty("user.dir");
        return projectRoot + File.separator + "uploads";
    }

    // 프로필/배경 이미지 조회
    public String getMemberImage(Long memberId, ImageType imageType) {
        Optional<MemberImageEntity> imageEntity = memberImageRepository
                .findByMemberEntity_IdAndImageType(memberId, imageType);

        if (imageEntity.isPresent()) {
            return imageEntity.get().getMemberImagePath();
        }

        // 기본 이미지 반환
        return getDefaultImage(imageType);
    }

    // 프로필/배경 이미지 업로드 및 저장
    public String uploadMemberImage(Long memberId, MultipartFile file, ImageType imageType)
            throws CommonExceptionTemplate {

        if (file == null || file.isEmpty()) {
            throw MemberException.INVALID_DATA.getException();
        }

        // 회원 존재 확인
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberException.NOT_EXIST_DATA.getException());

        try {
            // 파일 저장
            String filePath = saveFile(file, "member");

            // 기존 이미지 확인
            Optional<MemberImageEntity> existingImage = memberImageRepository
                    .findByMemberEntity_IdAndImageType(memberId, imageType);

            MemberImageEntity imageEntity;
            if (existingImage.isPresent()) {
                // 기존 이미지 삭제 후 업데이트
                imageEntity = existingImage.get();
                deleteFile(imageEntity.getMemberImagePath());

                imageEntity.setMemberImageName(file.getOriginalFilename());
                imageEntity.setMemberImagePath(filePath);
                imageEntity.setMemberImageSize(file.getSize());
            } else {
                // 새 이미지 생성
                imageEntity = new MemberImageEntity();
                imageEntity.setMemberEntity(memberEntity);
                imageEntity.setImageType(imageType);
                imageEntity.setMemberImageName(file.getOriginalFilename());
                imageEntity.setMemberImagePath(filePath);
                imageEntity.setMemberImageSize(file.getSize());
            }

            memberImageRepository.save(imageEntity);
            return filePath;

        } catch (IOException e) {
            throw new CommonExceptionTemplate(500, "파일 업로드에 실패했습니다.");
        }
    }

    // 파일 저장
    private String saveFile(MultipartFile file, String folder) throws IOException {
        String uploadBasePath = getUploadPath();
        String targetPath = uploadBasePath + File.separator + folder;

        File uploadDir = new File(targetPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;
        File destinationFile = new File(targetPath + File.separator + fileName);

        file.transferTo(destinationFile);
        return "/" + folder + "/" + fileName;
    }

    // 파일 삭제
    private void deleteFile(String filePath) {
        try {
            String uploadBasePath = getUploadPath();
            File file = new File(uploadBasePath + filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            System.err.println("파일 삭제 실패: " + filePath);
        }
    }

    // 기본 이미지 경로 반환
    private String getDefaultImage(ImageType imageType) {
        if (imageType == ImageType.profile) {
            return "/images/hotel_account_img/member.jpg";
        } else {
            return "/images/hotel_account_img/back.jpg";
        }
    }
}