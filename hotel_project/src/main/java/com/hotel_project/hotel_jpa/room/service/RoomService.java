package com.hotel_project.hotel_jpa.room.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.room.dto.*;
import com.hotel_project.hotel_jpa.room.mapper.RoomMapper;
import com.hotel_project.hotel_jpa.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public Page<RoomViewDto> findByName(Pageable pageable, String roomName) {
        List<RoomViewDto> content = roomMapper.findByName(roomName, pageable.getOffset(), pageable.getPageSize());
        long total = roomMapper.countByName(roomName);
        return new PageImpl<>(content, pageable, total);
    }

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

    public String insert(RoomDto roomDto) throws CommonExceptionTemplate {
        if (roomDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        RoomEntity entity = new RoomEntity();
        entity.copyMembers(roomDto);
        roomRepository.save(entity);
        return "insert ok";
    }

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

    // 날짜별 객실 재고 및 가격 조회
    // HotelThree.vue의 "잔여 객실" 섹션에서 사용
    // room_name으로 그룹핑하여 재고 개수 표시
    public List<RoomAvailabilityDto> getRoomAvailability(RoomSearchDto searchDto) {
        log.info("객실 재고 조회 - hotelId: {}, checkIn: {}, checkOut: {}",
                searchDto.getHotelId(), searchDto.getCheckIn(), searchDto.getCheckOut());

        try {
            // resultType이 map이므로 Map으로 받기
            List<Map<String, Object>> results = roomMapper.getRoomAvailabilityWithPricing(
                    searchDto.getHotelId(),
                    searchDto.getCheckIn(),
                    searchDto.getCheckOut()
            );

            if (results == null || results.isEmpty()) {
                log.warn("No available rooms found for hotelId: {}", searchDto.getHotelId());
                return new ArrayList<>();
            }

            // Map → DTO 변환
            return results.stream()
                    .map(map -> this.mapToRoomAvailabilityDto(map, searchDto))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error getting room availability", e);
            throw new RuntimeException("Failed to fetch room availability", e);
        }
    }

    //특정 객실의 날짜별 상세 가격 조회
    //Book Now 클릭 시 결제 화면에서 사용
    public List<Map<String, Object>> getRoomDailyPrices(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        log.info("객실 날짜별 가격 조회 - roomId: {}, checkIn: {}, checkOut: {}",
                roomId, checkIn, checkOut);

        try {
            List<Map<String, Object>> prices = roomMapper.getRoomDailyPrices(roomId, checkIn, checkOut);
            return prices != null ? prices : new ArrayList<>();

        } catch (Exception e) {
            log.error("Error getting daily prices for roomId: {}", roomId, e);
            throw new RuntimeException("Failed to fetch daily prices", e);
        }
    }

    //객실 상세 정보 조회 (Book Now용)
    //결제 화면으로 전달할 전체 정보
    public Map<String, Object> getRoomDetailById(Long roomId) {
        log.info("객실 상세 정보 조회 - roomId: {}", roomId);

        try {
            return roomMapper.getRoomDetailById(roomId);

        } catch (Exception e) {
            log.error("Error getting room detail for roomId: {}", roomId, e);
            throw new RuntimeException("Failed to fetch room detail", e);
        }
    }

    // Map → RoomAvailabilityDto 변환
    // MyBatis의 resultType=map에서 받은 Map 객체를 DTO로 변환
    private RoomAvailabilityDto mapToRoomAvailabilityDto(Map<String, Object> map, RoomSearchDto searchDto) {
        return RoomAvailabilityDto.builder()
                .roomId(convertToLong(map.get("roomId")))
                .roomName((String) map.get("roomName"))
                .bedType((String) map.get("bedType"))
                .basePrice((BigDecimal) map.get("basePrice"))
                .totalPrice((BigDecimal) map.get("totalPrice"))
                .nights(convertToInteger(map.get("nights")))
                .availableCount(convertToInteger(map.get("availableCount")))
                .hotelId(searchDto.getHotelId())
                .checkIn(searchDto.getCheckIn())
                .checkOut(searchDto.getCheckOut())
                .build();
    }

    // Object를 Long으로 안전하게 변환
    private Long convertToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    // Object를 Integer로 안전하게 변환
    private Integer convertToInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }
}