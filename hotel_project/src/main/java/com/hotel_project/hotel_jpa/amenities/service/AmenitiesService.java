package com.hotel_project.hotel_jpa.amenities.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesDto;
import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesEntity;
import com.hotel_project.hotel_jpa.amenities.mapper.AmenitiesMapper;
import com.hotel_project.hotel_jpa.amenities.repository.AmenitiesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AmenitiesService {

    private final AmenitiesRepository amenitiesRepository;
    private final AmenitiesMapper amenitiesMapper;

    // 이름 검색으로 통합 (페이지네이션 포함, 검색어 없으면 전체 조회)
    public Page<AmenitiesDto> findByName(Pageable pageable, String amenitiesName) {
        List<AmenitiesDto> content = amenitiesMapper.findByName(amenitiesName, pageable.getOffset(), pageable.getPageSize());
        long total = amenitiesMapper.countByName(amenitiesName);
        return new PageImpl<>(content, pageable, total);
    }

    // Repository로 변경
    public AmenitiesDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<AmenitiesEntity> entityOptional = amenitiesRepository.findById(id);
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        AmenitiesEntity entity = entityOptional.get();
        AmenitiesDto dto = new AmenitiesDto();
        dto.copyMembers(entity);
        return dto;
    }

    // INSERT - JPA 사용
    public String insert(AmenitiesDto amenitiesDto) throws CommonExceptionTemplate {
        if (amenitiesDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        if (amenitiesDto.getAmenitiesName() != null &&
                amenitiesRepository.existsByAmenitiesName(amenitiesDto.getAmenitiesName().trim())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        AmenitiesEntity entity = new AmenitiesEntity();
        entity.copyMembers(amenitiesDto);
        amenitiesRepository.save(entity);
        return "insert ok";
    }

    // UPDATE - JPA 사용
    public String update(AmenitiesDto amenitiesDto) throws CommonExceptionTemplate {
        if (amenitiesDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }
        if (amenitiesDto.getId() == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<AmenitiesEntity> entityOptional = amenitiesRepository.findById(amenitiesDto.getId());
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        AmenitiesEntity entity = entityOptional.get();

        if (amenitiesDto.getAmenitiesName() != null &&
                !amenitiesDto.getAmenitiesName().equals(entity.getAmenitiesName()) &&
                amenitiesRepository.existsByAmenitiesNameAndIdNot(amenitiesDto.getAmenitiesName(), amenitiesDto.getId())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        entity.copyNotNullMembers(amenitiesDto);
        amenitiesRepository.save(entity);
        return "update ok";
    }

    // DELETE - JPA 사용
    public String delete(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }
        if (!amenitiesRepository.existsById(id)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }
        amenitiesRepository.deleteById(id);
        return "delete ok";
    }
}