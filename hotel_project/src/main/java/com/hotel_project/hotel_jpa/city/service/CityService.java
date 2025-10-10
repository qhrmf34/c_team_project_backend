package com.hotel_project.hotel_jpa.city.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.city.dto.CityDto;
import com.hotel_project.hotel_jpa.city.dto.CityHotelOneDto;
import com.hotel_project.hotel_jpa.city.dto.CityViewDto;
import com.hotel_project.hotel_jpa.city.dto.CityEntity;
import com.hotel_project.hotel_jpa.city.mapper.CityMapper;
import com.hotel_project.hotel_jpa.city.repository.CityRepository;
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
public class CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    // 이름 검색으로 통합 (페이지네이션 포함, 검색어 없으면 전체 조회) - ViewDto 반환
    public Page<CityViewDto> findByName(Pageable pageable, String cityName) {
        List<CityViewDto> content = cityMapper.findByName(cityName, pageable.getOffset(), pageable.getPageSize());
        long total = cityMapper.countByName(cityName);
        return new PageImpl<>(content, pageable, total);
    }

    // Repository로 변경 - 기존 CityDto 반환 (수정/등록용)
    public CityDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<CityEntity> entityOptional = cityRepository.findById(id);
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        CityEntity entity = entityOptional.get();
        CityDto dto = new CityDto();
        dto.copyMembers(entity);
        return dto;
    }

    // INSERT - JPA 사용
    public String insert(CityDto cityDto) throws CommonExceptionTemplate {
        if (cityDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        if (cityDto.getCityName() != null &&
                cityRepository.existsByCityName(cityDto.getCityName().trim())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        CityEntity entity = new CityEntity();
        entity.copyMembers(cityDto);
        cityRepository.save(entity);
        return "insert ok";
    }

    // UPDATE - JPA 사용
    public String update(CityDto cityDto) throws CommonExceptionTemplate {
        if (cityDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }
        if (cityDto.getId() == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<CityEntity> entityOptional = cityRepository.findById(cityDto.getId());
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        CityEntity entity = entityOptional.get();

        if (cityDto.getCityName() != null &&
                !cityDto.getCityName().equals(entity.getCityName()) &&
                cityRepository.existsByCityNameAndIdNot(cityDto.getCityName(), cityDto.getId())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        entity.copyNotNullMembers(cityDto);
        cityRepository.save(entity);
        return "update ok";
    }

    // DELETE - JPA 사용
    public String delete(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }
        if (!cityRepository.existsById(id)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }
        cityRepository.deleteById(id);
        return "delete ok";
    }
    // 추천 도시 목록 조회
    public List<CityHotelOneDto> findFeaturedCities(int limit) {
        return cityMapper.findFeaturedCities(limit);
    }
}
