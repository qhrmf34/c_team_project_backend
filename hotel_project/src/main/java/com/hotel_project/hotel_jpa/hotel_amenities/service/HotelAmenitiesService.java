package com.hotel_project.hotel_jpa.hotel_amenities.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.hotel_amenities.dto.HotelAmenitiesDto;
import com.hotel_project.hotel_jpa.hotel_amenities.dto.HotelAmenitiesEntity;
import com.hotel_project.hotel_jpa.hotel_amenities.dto.HotelAmenitiesViewDto;
import com.hotel_project.hotel_jpa.hotel_amenities.mapper.HotelAmenitiesMapper;
import com.hotel_project.hotel_jpa.hotel_amenities.repository.HotelAmenitiesRepository;
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
public class HotelAmenitiesService {

    private final HotelAmenitiesRepository hotelAmenitiesRepository;
    private final HotelAmenitiesMapper hotelAmenitiesMapper;

    // 이름 검색으로 통합 (페이지네이션 포함, 검색어 없으면 전체 조회) - ViewDto 반환
    public Page<HotelAmenitiesViewDto> findByName(Pageable pageable, String searchQuery) {
        List<HotelAmenitiesViewDto> content = hotelAmenitiesMapper.findByName(searchQuery, pageable.getOffset(), pageable.getPageSize());
        long total = hotelAmenitiesMapper.countByName(searchQuery);
        return new PageImpl<>(content, pageable, total);
    }

    // Repository로 변경 - 기존 HotelAmenitiesDto 반환 (수정/등록용)
    public HotelAmenitiesDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<HotelAmenitiesEntity> entityOptional = hotelAmenitiesRepository.findById(id);
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        HotelAmenitiesEntity entity = entityOptional.get();
        HotelAmenitiesDto dto = new HotelAmenitiesDto();
        dto.copyMembers(entity);
        return dto;
    }

    // INSERT - JPA 사용 (연결 테이블 특성에 맞게 수정)
    public String insert(HotelAmenitiesDto hotelAmenitiesDto) throws CommonExceptionTemplate {
        if (hotelAmenitiesDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        // 필수 필드 체크
        if (hotelAmenitiesDto.getHotelId() == null || hotelAmenitiesDto.getAmenitiesId() == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        // 중복 체크 (같은 호텔에 같은 편의시설이 이미 등록되어 있는지)
        // 엔티티 관계를 통해 접근
        if (hotelAmenitiesRepository.existsByHotelEntity_IdAndAmenitiesEntity_Id(
                hotelAmenitiesDto.getHotelId(), hotelAmenitiesDto.getAmenitiesId())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        HotelAmenitiesEntity entity = new HotelAmenitiesEntity();
        entity.copyMembers(hotelAmenitiesDto);
        hotelAmenitiesRepository.save(entity);
        return "insert ok";
    }

    // UPDATE - JPA 사용
    public String update(HotelAmenitiesDto hotelAmenitiesDto) throws CommonExceptionTemplate {
        if (hotelAmenitiesDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }
        if (hotelAmenitiesDto.getId() == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<HotelAmenitiesEntity> entityOptional = hotelAmenitiesRepository.findById(hotelAmenitiesDto.getId());
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        HotelAmenitiesEntity entity = entityOptional.get();

        // 호텔이나 편의시설이 변경된 경우 중복 체크
        if (hotelAmenitiesDto.getHotelId() != null && hotelAmenitiesDto.getAmenitiesId() != null) {
            if (!hotelAmenitiesDto.getHotelId().equals(entity.getHotelId()) ||
                    !hotelAmenitiesDto.getAmenitiesId().equals(entity.getAmenitiesId())) {

                // 엔티티 관계를 통해 접근
                if (hotelAmenitiesRepository.existsByHotelEntity_IdAndAmenitiesEntity_IdAndIdNot(
                        hotelAmenitiesDto.getHotelId(), hotelAmenitiesDto.getAmenitiesId(), hotelAmenitiesDto.getId())) {
                    throw MemberException.DUPLICATE_DATA.getException();
                }
            }
        }

        entity.copyNotNullMembers(hotelAmenitiesDto);
        hotelAmenitiesRepository.save(entity);
        return "update ok";
    }

    // DELETE - JPA 사용
    public String delete(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }
        if (!hotelAmenitiesRepository.existsById(id)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }
        hotelAmenitiesRepository.deleteById(id);
        return "delete ok";
    }
}