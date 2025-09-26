package com.hotel_project.hotel_jpa.city_image.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.city_image.dto.CityImageDto;
import com.hotel_project.hotel_jpa.city_image.dto.CityImageViewDto;
import com.hotel_project.hotel_jpa.city_image.dto.CityImageEntity;
import com.hotel_project.hotel_jpa.city_image.mapper.CityImageMapper;
import com.hotel_project.hotel_jpa.city_image.repository.CityImageRepository;
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
public class CityImageService {

    private final CityImageRepository cityImageRepository;
    private final CityImageMapper cityImageMapper;

    private String getUploadPath() {
        String projectRoot = System.getProperty("user.dir");
        return projectRoot + File.separator + "uploads";
    }

    // 이름 검색으로 통합 (페이지네이션 포함, 검색어 없으면 전체 조회) - ViewDto 반환
    public Page<CityImageViewDto> findByName(Pageable pageable, String cityName) {
        List<CityImageViewDto> content = cityImageMapper.findByName(cityName, pageable.getOffset(), pageable.getPageSize());
        long total = cityImageMapper.countByName(cityName);
        return new PageImpl<>(content, pageable, total);
    }

    // Repository로 변경 - 기존 CityImageDto 반환 (수정/등록용)
    public CityImageDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<CityImageEntity> entityOptional = cityImageRepository.findById(id);
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        CityImageEntity entity = entityOptional.get();
        CityImageDto dto = new CityImageDto();
        dto.copyMembers(entity);
        return dto;
    }

    // INSERT - JPA 사용
    public String insert(CityImageDto cityImageDto) throws CommonExceptionTemplate {
        if (cityImageDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        CityImageEntity entity = new CityImageEntity();
        entity.copyMembers(cityImageDto);
        cityImageRepository.save(entity);
        return "insert ok";
    }

    // UPDATE - JPA 사용
    public String update(CityImageDto cityImageDto) throws CommonExceptionTemplate {
        if (cityImageDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }
        if (cityImageDto.getId() == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<CityImageEntity> entityOptional = cityImageRepository.findById(cityImageDto.getId());
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        CityImageEntity entity = entityOptional.get();
        entity.copyNotNullMembers(cityImageDto);
        cityImageRepository.save(entity);
        return "update ok";
    }

    // DELETE - JPA 사용
    public String delete(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }
        if (!cityImageRepository.existsById(id)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        Optional<CityImageEntity> entity = cityImageRepository.findById(id);
        if (entity.isPresent() && entity.get().getCityImagePath() != null) {
            deleteFile(entity.get().getCityImagePath());
        }

        cityImageRepository.deleteById(id);
        return "delete ok";
    }

    // 파일 업로드
    public String uploadFile(MultipartFile file) throws CommonExceptionTemplate {
        if (file == null || file.isEmpty()) {
            throw MemberException.INVALID_DATA.getException();
        }

        try {
            String uploadBasePath = getUploadPath();
            String cityUploadPath = uploadBasePath + File.separator + "city";

            File uploadDir = new File(cityUploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String originalFileName = file.getOriginalFilename();
            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + extension;
            File destinationFile = new File(cityUploadPath + File.separator + fileName);

            file.transferTo(destinationFile);
            return "/city/" + fileName;

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
}