package com.hotel_project.hotel_jpa.hotel.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.hotel.dto.HotelDto;
import com.hotel_project.hotel_jpa.hotel.dto.HotelEntity;
import com.hotel_project.hotel_jpa.hotel.dto.HotelViewDto;
import com.hotel_project.hotel_jpa.hotel.mapper.HotelMapper;
import com.hotel_project.hotel_jpa.hotel.repository.HotelRepository;
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
public class HotelService {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    // 이름 검색으로 통합 (페이지네이션 포함, 검색어 없으면 전체 조회) - ViewDto 반환
    public Page<HotelViewDto> findByName(Pageable pageable, String hotelName) {
        List<HotelViewDto> content = hotelMapper.findByName(hotelName, pageable.getOffset(), pageable.getPageSize());
        long total = hotelMapper.countByName(hotelName);
        return new PageImpl<>(content, pageable, total);
    }

    // Repository로 변경 - 기존 HotelDto 반환 (수정/등록용)
    public HotelDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<HotelEntity> entityOptional = hotelRepository.findById(id);
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        HotelEntity entity = entityOptional.get();
        HotelDto dto = new HotelDto();
        dto.copyMembers(entity);
        return dto;
    }

    // INSERT - JPA 사용
    public String insert(HotelDto hotelDto) throws CommonExceptionTemplate {
        if (hotelDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        if (hotelDto.getHotelName() != null &&
                hotelRepository.existsByHotelName(hotelDto.getHotelName().trim())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        HotelEntity entity = new HotelEntity();
        entity.copyMembers(hotelDto);
        hotelRepository.save(entity);
        return "insert ok";
    }

    // UPDATE - JPA 사용
    public String update(HotelDto hotelDto) throws CommonExceptionTemplate {
        if (hotelDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }
        if (hotelDto.getId() == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<HotelEntity> entityOptional = hotelRepository.findById(hotelDto.getId());
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        HotelEntity entity = entityOptional.get();

        if (hotelDto.getHotelName() != null &&
                !hotelDto.getHotelName().equals(entity.getHotelName()) &&
                hotelRepository.existsByHotelNameAndIdNot(hotelDto.getHotelName(), hotelDto.getId())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        entity.copyNotNullMembers(hotelDto);
        hotelRepository.save(entity);
        return "update ok";
    }

    // DELETE - JPA 사용
    public String delete(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }
        if (!hotelRepository.existsById(id)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }
        hotelRepository.deleteById(id);
        return "delete ok";
    }
}
