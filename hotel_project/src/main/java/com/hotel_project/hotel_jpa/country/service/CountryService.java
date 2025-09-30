package com.hotel_project.hotel_jpa.country.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.country.dto.CountryDto;
import com.hotel_project.hotel_jpa.country.dto.CountryEntity;
import com.hotel_project.hotel_jpa.country.mapper.CountryMapper;
import com.hotel_project.hotel_jpa.country.repository.CountryRepository;
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
public class CountryService {

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    // 이름 검색으로 통합 (페이지네이션 포함, 검색어 없으면 전체 조회)
    public Page<CountryDto> findByName(Pageable pageable, String countryName) {
        List<CountryDto> content = countryMapper.findByName(countryName, pageable.getOffset(), pageable.getPageSize());
        long total = countryMapper.countByName(countryName);
        return new PageImpl<>(content, pageable, total);
    }

    // Repository로 변경
    public CountryDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<CountryEntity> entityOptional = countryRepository.findById(id);
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        CountryEntity entity = entityOptional.get();
        CountryDto dto = new CountryDto();
        dto.copyMembers(entity);
        return dto;
    }

    // INSERT - JPA 사용
    public String insert(CountryDto countryDto) throws CommonExceptionTemplate {
        if (countryDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        if (countryDto.getCountryName() != null &&
                countryRepository.existsByCountryName(countryDto.getCountryName().trim())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        CountryEntity entity = new CountryEntity();
        entity.copyMembers(countryDto);
        countryRepository.save(entity);
        return "insert ok";
    }

    // UPDATE - JPA 사용
    public String update(CountryDto countryDto) throws CommonExceptionTemplate {
        if (countryDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }
        if (countryDto.getId() == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<CountryEntity> entityOptional = countryRepository.findById(countryDto.getId());
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        CountryEntity entity = entityOptional.get();

        if (countryDto.getCountryName() != null &&
                !countryDto.getCountryName().equals(entity.getCountryName()) &&
                countryRepository.existsByCountryNameAndIdNot(countryDto.getCountryName(), countryDto.getId())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        entity.copyNotNullMembers(countryDto);
        countryRepository.save(entity);
        return "update ok";
    }

    // DELETE - JPA 사용
    public String delete(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }
        if (!countryRepository.existsById(id)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }
        countryRepository.deleteById(id);
        return "delete ok";
    }
}