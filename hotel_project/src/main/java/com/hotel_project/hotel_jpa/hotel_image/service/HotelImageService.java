package com.hotel_project.hotel_jpa.hotel_image.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.hotel_image.dto.HotelImageDto;
import com.hotel_project.hotel_jpa.hotel_image.dto.HotelImageEntity;
import com.hotel_project.hotel_jpa.hotel_image.dto.HotelImageViewDto;
import com.hotel_project.hotel_jpa.hotel_image.mapper.HotelImageMapper;
import com.hotel_project.hotel_jpa.hotel_image.repository.HotelImageRepository;
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
public class HotelImageService {

    private final HotelImageRepository hotelImageRepository;
    private final HotelImageMapper hotelImageMapper;

    private String getUploadPath() {
        String projectRoot = System.getProperty("user.dir");
        return projectRoot + File.separator + "uploads";
    }

    // 이름 검색으로 통합 (페이지네이션 포함, 검색어 없으면 전체 조회) - ViewDto 반환
    public Page<HotelImageViewDto> findByName(Pageable pageable, String hotelName) {
        List<HotelImageViewDto> content = hotelImageMapper.findByName(hotelName, pageable.getOffset(), pageable.getPageSize());
        long total = hotelImageMapper.countByName(hotelName);
        return new PageImpl<>(content, pageable, total);
    }

    // Repository로 변경 - 기존 HotelImageDto 반환 (수정/등록용)
    public HotelImageDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<HotelImageEntity> entityOptional = hotelImageRepository.findById(id);
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        HotelImageEntity entity = entityOptional.get();
        HotelImageDto dto = new HotelImageDto();
        dto.copyMembers(entity);
        return dto;
    }

    // INSERT - JPA 사용
    public String insert(HotelImageDto hotelImageDto) throws CommonExceptionTemplate {
        if (hotelImageDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        HotelImageEntity entity = new HotelImageEntity();
        entity.copyMembers(hotelImageDto);
        hotelImageRepository.save(entity);
        return "insert ok";
    }

    // UPDATE - JPA 사용
    public String update(HotelImageDto hotelImageDto) throws CommonExceptionTemplate {
        if (hotelImageDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }
        if (hotelImageDto.getId() == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<HotelImageEntity> entityOptional = hotelImageRepository.findById(hotelImageDto.getId());
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }
        if (entityOptional.isPresent() && entityOptional.get().getHotelImagePath() != null) {
            deleteFile(entityOptional.get().getHotelImagePath());
        }
        HotelImageEntity entity = entityOptional.get();
        entity.copyNotNullMembers(hotelImageDto);
        hotelImageRepository.save(entity);
        return "update ok";
    }

    // DELETE - JPA 사용
    public String delete(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }
        if (!hotelImageRepository.existsById(id)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        Optional<HotelImageEntity> entity = hotelImageRepository.findById(id);
        if (entity.isPresent() && entity.get().getHotelImagePath() != null) {
            deleteFile(entity.get().getHotelImagePath());
        }

        hotelImageRepository.deleteById(id);
        return "delete ok";
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
}