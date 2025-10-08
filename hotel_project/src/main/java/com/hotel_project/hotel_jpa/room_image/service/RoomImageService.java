package com.hotel_project.hotel_jpa.room_image.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.room_image.dto.RoomImageDto;
import com.hotel_project.hotel_jpa.room_image.dto.RoomImageEntity;
import com.hotel_project.hotel_jpa.room_image.dto.RoomImageViewDto;
import com.hotel_project.hotel_jpa.room_image.mapper.RoomImageMapper;
import com.hotel_project.hotel_jpa.room_image.repository.RoomImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomImageService {

    private final RoomImageRepository roomImageRepository;
    private final RoomImageMapper roomImageMapper;

    private String getUploadPath() {
        String projectRoot = System.getProperty("user.dir");
        return projectRoot + File.separator + "uploads";
    }

    // 이름 검색으로 통합 (페이지네이션 포함, 검색어 없으면 전체 조회) - ViewDto 반환
    public Page<RoomImageViewDto> findByName(Pageable pageable, String roomName) {
        List<RoomImageViewDto> content = roomImageMapper.findByName(roomName, pageable.getOffset(), pageable.getPageSize());
        long total = roomImageMapper.countByName(roomName);
        return new PageImpl<>(content, pageable, total);
    }

    // Repository로 변경 - 기존 RoomImageDto 반환 (수정/등록용)
    public RoomImageDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<RoomImageEntity> entityOptional = roomImageRepository.findById(id);
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        RoomImageEntity entity = entityOptional.get();
        RoomImageDto dto = new RoomImageDto();
        dto.copyMembers(entity);
        return dto;
    }

    // INSERT - JPA 사용
    public String insert(RoomImageDto roomImageDto) throws CommonExceptionTemplate {
        if (roomImageDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        RoomImageEntity entity = new RoomImageEntity();
        entity.copyMembers(roomImageDto);
        roomImageRepository.save(entity);
        return "insert ok";
    }

    // UPDATE - JPA 사용
    public String update(RoomImageDto roomImageDto) throws CommonExceptionTemplate {
        if (roomImageDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }
        if (roomImageDto.getId() == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<RoomImageEntity> entityOptional = roomImageRepository.findById(roomImageDto.getId());
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        RoomImageEntity entity = entityOptional.get();
        entity.copyNotNullMembers(roomImageDto);
        roomImageRepository.save(entity);
        return "update ok";
    }

    // DELETE - JPA 사용
    public String delete(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }
        if (!roomImageRepository.existsById(id)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        Optional<RoomImageEntity> entity = roomImageRepository.findById(id);
        if (entity.isPresent() && entity.get().getRoomImagePath() != null) {
            deleteFile(entity.get().getRoomImagePath());
        }

        roomImageRepository.deleteById(id);
        return "delete ok";
    }

    // 파일 업로드
    public String uploadFile(MultipartFile file) throws CommonExceptionTemplate {
        if (file == null || file.isEmpty()) {
            throw MemberException.INVALID_DATA.getException();
        }

        try {
            String uploadBasePath = getUploadPath();
            String roomUploadPath = uploadBasePath + File.separator + "room";

            File uploadDir = new File(roomUploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + extension;
            File destinationFile = new File(roomUploadPath + File.separator + fileName);

            file.transferTo(destinationFile);
            return "/room/" + fileName;

        } catch (IOException e) {
            throw new CommonExceptionTemplate(500, "파일 업로드에 실패했습니다.");
        }
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

    // 객실별 이미지 조회 - MyBatis Mapper 사용
    public List<RoomImageDto> findByRoomId(Long roomId) throws CommonExceptionTemplate {
        if (roomId == null) {
            throw MemberException.INVALID_ID.getException();
        }

        return roomImageMapper.findByRoomId(roomId);
    }
}