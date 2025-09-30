package com.hotel_project.hotel_jpa.room_pricing.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.room_pricing.dto.RoomPricingDto;
import com.hotel_project.hotel_jpa.room_pricing.dto.RoomPricingEntity;
import com.hotel_project.hotel_jpa.room_pricing.dto.RoomPricingViewDto;
import com.hotel_project.hotel_jpa.room_pricing.mapper.RoomPricingMapper;
import com.hotel_project.hotel_jpa.room_pricing.repository.RoomPricingRepository;
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
public class RoomPricingService {

    private final RoomPricingRepository roomPricingRepository;
    private final RoomPricingMapper roomPricingMapper;

    // 이름 검색으로 통합 (페이지네이션 포함, 검색어 없으면 전체 조회) - ViewDto 반환
    public Page<RoomPricingViewDto> findByName(Pageable pageable, String roomName) {
        List<RoomPricingViewDto> content = roomPricingMapper.findByName(
                roomName,
                pageable.getOffset(),
                pageable.getPageSize()
        );
        long total = roomPricingMapper.countByName(roomName);
        return new PageImpl<>(content, pageable, total);
    }

    // Repository로 변경 - 기존 RoomPricingDto 반환 (수정/등록용)
    public RoomPricingDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<RoomPricingEntity> entityOptional = roomPricingRepository.findById(id);
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        RoomPricingEntity entity = entityOptional.get();
        RoomPricingDto dto = new RoomPricingDto();
        dto.copyMembers(entity);
        return dto;
    }

    // INSERT - JPA 사용
    public String insert(RoomPricingDto roomPricingDto) throws CommonExceptionTemplate {
        if (roomPricingDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        // 유효성 검증
        if (roomPricingDto.getRoomId() == null) {
            throw new CommonExceptionTemplate(400, "객실 ID는 필수입니다.");
        }
        if (roomPricingDto.getDate() == null) {
            throw new CommonExceptionTemplate(400, "날짜는 필수입니다.");
        }
        if (roomPricingDto.getPrice() == null) {
            throw new CommonExceptionTemplate(400, "가격은 필수입니다.");
        }

        // 중복 체크: 같은 객실의 같은 날짜에 이미 가격이 존재하는지 확인
        boolean exists = roomPricingRepository.existsByRoomEntityIdAndDate(
                roomPricingDto.getRoomId(),
                roomPricingDto.getDate()
        );

        if (exists) {
            throw new CommonExceptionTemplate(400, "해당 객실의 해당 날짜에 이미 가격이 설정되어 있습니다.");
        }

        RoomPricingEntity entity = new RoomPricingEntity();
        entity.copyMembers(roomPricingDto);
        roomPricingRepository.save(entity);
        return "insert ok";
    }

    // UPDATE - JPA 사용
    public String update(RoomPricingDto roomPricingDto) throws CommonExceptionTemplate {
        if (roomPricingDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }
        if (roomPricingDto.getId() == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<RoomPricingEntity> entityOptional = roomPricingRepository.findById(roomPricingDto.getId());
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        // 날짜나 객실이 변경되는 경우 중복 체크
        if (roomPricingDto.getRoomId() != null && roomPricingDto.getDate() != null) {
            boolean exists = roomPricingRepository.existsByRoomEntityIdAndDateAndIdNot(
                    roomPricingDto.getRoomId(),
                    roomPricingDto.getDate(),
                    roomPricingDto.getId()
            );

            if (exists) {
                throw new CommonExceptionTemplate(400, "해당 객실의 해당 날짜에 이미 가격이 설정되어 있습니다.");
            }
        }

        RoomPricingEntity entity = entityOptional.get();
        entity.copyNotNullMembers(roomPricingDto);
        roomPricingRepository.save(entity);
        return "update ok";
    }

    // DELETE - JPA 사용
    public String delete(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }
        if (!roomPricingRepository.existsById(id)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        roomPricingRepository.deleteById(id);
        return "delete ok";
    }
}