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

    // SELECT - MyBatis 사용 (페이지네이션 포함)
    public Page<CountryDto> findAll(Pageable pageable, String search) {
        List<CountryDto> content = countryMapper.findAll(search, pageable.getOffset(), pageable.getPageSize());
        long total = countryMapper.countAll(search);
        return new PageImpl<>(content, pageable, total);
    }

    public CountryDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }
        CountryDto countryDto = countryMapper.findById(id);
        if (countryDto == null) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }
        return countryDto;
    }

    public List<CountryDto> findByName(String countryName) throws CommonExceptionTemplate {
        if (countryName == null || countryName.trim().isEmpty()) {
            throw MemberException.INVALID_DATA.getException();
        }
        return countryMapper.findByName(countryName);
    }

    // INSERT - JPA 사용
    public String insert(CountryDto countryDto) throws CommonExceptionTemplate {
        if (countryDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        // 중복 체크
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

        // 중복 체크(이름이 변경될 때만)
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

