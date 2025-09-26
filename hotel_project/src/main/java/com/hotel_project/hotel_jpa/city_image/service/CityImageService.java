package com.hotel_project.hotel_jpa.city_image.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.city_image.dto.CityImageDto;
import com.hotel_project.hotel_jpa.city_image.dto.CityImageEntity;
import com.hotel_project.hotel_jpa.city_image.mapper.CityImageMapper;
import com.hotel_project.hotel_jpa.city_image.repository.CityImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    // SELECT - MyBatis 사용 (페이지네이션 포함)
    public Page<CityImageDto> findAll(Pageable pageable, String search) {
        List<CityImageDto> content = cityImageMapper.findAll(search, pageable.getOffset(), pageable.getPageSize());
        long total = cityImageMapper.countAll(search);
        return new PageImpl<>(content, pageable, total);
    }

    public CityImageDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }
        CityImageDto cityImageDto = cityImageMapper.findById(id);
        if (cityImageDto == null) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }
        return cityImageDto;
    }

    public List<CityImageDto> findByName(String cityName) throws CommonExceptionTemplate {
        if (cityName == null || cityName.trim().isEmpty()) {
            throw MemberException.INVALID_DATA.getException();
        }
        return cityImageMapper.findByName(cityName);
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

        // 파일도 함께 삭제
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
            // 절대 경로 사용
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

