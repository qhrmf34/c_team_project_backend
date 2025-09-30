package com.hotel_project.hotel_jpa.room_image.mapper;

import com.hotel_project.hotel_jpa.hotel_image.dto.HotelImageViewDto;
import com.hotel_project.hotel_jpa.room_image.dto.RoomImageViewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomImageMapper {

    // 이름 검색 통합 (페이지네이션, 검색어 없으면 전체 조회) - ViewDto 반환
    List<RoomImageViewDto> findByName(@Param("roomName") String roomName,
                                      @Param("offset") long offset,
                                      @Param("size") int size);

    // 개수 조회 (검색어 없으면 전체 개수)
    long countByName(@Param("roomName") String roomName);
}