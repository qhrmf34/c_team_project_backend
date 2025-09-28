package com.hotel_project.hotel_jpa.room.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.room.dto.RoomDto;
import com.hotel_project.hotel_jpa.room.dto.RoomEntity;
import com.hotel_project.hotel_jpa.room.dto.RoomViewDto;
import com.hotel_project.hotel_jpa.room.mapper.RoomMapper;
import com.hotel_project.hotel_jpa.room.repository.RoomRepository;
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
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    // 이름 검색으로 통합 (페이지네이션 포함, 검색어 없으면 전체 조회) - ViewDto 반환
    public Page<RoomViewDto> findByName(Pageable pageable, String roomName) {
        List<RoomViewDto> content = roomMapper.findByName(roomName, pageable.getOffset(), pageable.getPageSize());
        long total = roomMapper.countByName(roomName);
        return new PageImpl<>(content, pageable, total);
    }

    // Repository로 변경 - 기존 roomDto 반환 (수정/등록용)
    public RoomDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<RoomEntity> entityOptional = roomRepository.findById(id);
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        RoomEntity entity = entityOptional.get();
        RoomDto dto = new RoomDto();
        dto.copyMembers(entity);
        return dto;
    }

    // INSERT - JPA 사용
    public String insert(RoomDto roomDto) throws CommonExceptionTemplate {
        if (roomDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        if (roomDto.getRoomName() != null &&
                roomRepository.existsByRoomName(roomDto.getRoomName().trim())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        RoomEntity entity = new RoomEntity();
        entity.copyMembers(roomDto);
        roomRepository.save(entity);
        return "insert ok";
    }

    // UPDATE - JPA 사용
    public String update(RoomDto roomDto) throws CommonExceptionTemplate {
        if (roomDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }
        if (roomDto.getId() == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<RoomEntity> entityOptional = roomRepository.findById(roomDto.getId());
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        RoomEntity entity = entityOptional.get();

        if (roomDto.getRoomName() != null &&
                !roomDto.getRoomName().equals(entity.getRoomName()) &&
                roomRepository.existsByRoomNameAndIdNot(roomDto.getRoomName(), roomDto.getId())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        entity.copyNotNullMembers(roomDto);
        roomRepository.save(entity);
        return "update ok";
    }

    // DELETE - JPA 사용
    public String delete(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }
        if (!roomRepository.existsById(id)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }
        roomRepository.deleteById(id);
        return "delete ok";
    }
}
