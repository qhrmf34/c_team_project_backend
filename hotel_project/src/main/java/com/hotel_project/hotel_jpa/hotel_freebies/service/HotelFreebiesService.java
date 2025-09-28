package com.hotel_project.hotel_jpa.hotel_freebies.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.hotel_freebies.dto.HotelFreebiesDto;
import com.hotel_project.hotel_jpa.hotel_freebies.dto.HotelFreebiesEntity;
import com.hotel_project.hotel_jpa.hotel_freebies.dto.HotelFreebiesViewDto;
import com.hotel_project.hotel_jpa.hotel_freebies.mapper.HotelFreebiesMapper;
import com.hotel_project.hotel_jpa.hotel_freebies.repository.HotelFreebiesRepository;
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
public class HotelFreebiesService {

    private final HotelFreebiesRepository hotelFreebiesRepository;
    private final HotelFreebiesMapper hotelFreebiesMapper;

    // 이름 검색으로 통합 (페이지네이션 포함, 검색어 없으면 전체 조회) - ViewDto 반환
    public Page<HotelFreebiesViewDto> findByName(Pageable pageable, String searchQuery) {
        List<HotelFreebiesViewDto> content = hotelFreebiesMapper.findByName(searchQuery, pageable.getOffset(), pageable.getPageSize());
        long total = hotelFreebiesMapper.countByName(searchQuery);
        return new PageImpl<>(content, pageable, total);
    }

    // Repository로 변경 - 기존 HotelFreebiesDto 반환 (수정/등록용)
    public HotelFreebiesDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<HotelFreebiesEntity> entityOptional = hotelFreebiesRepository.findById(id);
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        HotelFreebiesEntity entity = entityOptional.get();
        HotelFreebiesDto dto = new HotelFreebiesDto();
        dto.copyMembers(entity);
        return dto;
    }

    // INSERT - JPA 사용 (연결 테이블 특성에 맞게 수정)
    public String insert(HotelFreebiesDto hotelFreebiesDto) throws CommonExceptionTemplate {
        if (hotelFreebiesDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        // 필수 필드 체크
        if (hotelFreebiesDto.getHotelId() == null || hotelFreebiesDto.getFreebiesId() == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        // 중복 체크 (같은 호텔에 같은 무료시설이 이미 등록되어 있는지)
        // 엔티티 관계를 통해 접근
        if (hotelFreebiesRepository.existsByHotelEntity_IdAndFreebiesEntity_Id(
                hotelFreebiesDto.getHotelId(), hotelFreebiesDto.getFreebiesId())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        HotelFreebiesEntity entity = new HotelFreebiesEntity();
        entity.copyMembers(hotelFreebiesDto);
        hotelFreebiesRepository.save(entity);
        return "insert ok";
    }

    // UPDATE - JPA 사용
    public String update(HotelFreebiesDto hotelFreebiesDto) throws CommonExceptionTemplate {
        if (hotelFreebiesDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }
        if (hotelFreebiesDto.getId() == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<HotelFreebiesEntity> entityOptional = hotelFreebiesRepository.findById(hotelFreebiesDto.getId());
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        HotelFreebiesEntity entity = entityOptional.get();

        // 호텔이나 무료시설이 변경된 경우 중복 체크
        if (hotelFreebiesDto.getHotelId() != null && hotelFreebiesDto.getFreebiesId() != null) {
            if (!hotelFreebiesDto.getHotelId().equals(entity.getHotelId()) ||
                    !hotelFreebiesDto.getFreebiesId().equals(entity.getFreebiesId())) {

                // 엔티티 관계를 통해 접근
                if (hotelFreebiesRepository.existsByHotelEntity_IdAndFreebiesEntity_IdAndIdNot(
                        hotelFreebiesDto.getHotelId(), hotelFreebiesDto.getFreebiesId(), hotelFreebiesDto.getId())) {
                    throw MemberException.DUPLICATE_DATA.getException();
                }
            }
        }

        entity.copyNotNullMembers(hotelFreebiesDto);
        hotelFreebiesRepository.save(entity);
        return "update ok";
    }

    // DELETE - JPA 사용
    public String delete(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }
        if (!hotelFreebiesRepository.existsById(id)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }
        hotelFreebiesRepository.deleteById(id);
        return "delete ok";
    }
}