package com.hotel_project.hotel_jpa.room.mapper;

import com.hotel_project.hotel_jpa.room.dto.RoomAvailabilityDto;
import com.hotel_project.hotel_jpa.room.dto.RoomViewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface RoomMapper {

    List<RoomViewDto> findByName(
            @Param("search") String roomName,
            @Param("offset") long offset,
            @Param("size") int size
    );

    long countByName(@Param("search") String roomName);

    // 날짜별 객실 재고 확인 및 총 가격 계산
    List<RoomAvailabilityDto> getRoomAvailabilityWithPricing(
            @Param("hotelId") Long hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    //특정 객실의 날짜별 상세 가격 조회
    // @return Map { date, price }
    List<Map<String, Object>> getRoomDailyPrices(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

     // 객실 이미지 조회
     // @return Map { id, roomImagePath, createdAt }
     List<Map<String, Object>> getRoomImages(@Param("roomId") Long roomId);

     //객실 상세 정보 조회 (Book Now용)
     // @return Map { roomId, roomName, basePrice, bedType, maxGuests, roomView,
     // hotelId, hotelName, address, checkInTime, checkOutTime, image }
    Map<String, Object> getRoomDetailById(@Param("roomId") Long roomId);
}