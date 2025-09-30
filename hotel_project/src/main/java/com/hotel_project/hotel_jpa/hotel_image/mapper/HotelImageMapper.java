package com.hotel_project.hotel_jpa.hotel_image.mapper;

import com.hotel_project.hotel_jpa.hotel_image.dto.HotelImageViewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HotelImageMapper {

    // 이름 검색 통합 (페이지네이션, 검색어 없으면 전체 조회) - ViewDto 반환
    List<HotelImageViewDto> findByName(@Param("hotelName") String hotelName,
                                       @Param("offset") long offset,
                                       @Param("size") int size);

    // 개수 조회 (검색어 없으면 전체 개수)
    long countByName(@Param("hotelName") String hotelName);
}