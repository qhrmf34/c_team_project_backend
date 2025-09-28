package com.hotel_project.hotel_jpa.hotel_freebies.mapper;

import com.hotel_project.hotel_jpa.hotel_freebies.dto.HotelFreebiesViewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HotelFreebiesMapper {

    // 이름 검색 통합 (페이지네이션, 검색어 없으면 전체 조회) - ViewDto 반환
    List<HotelFreebiesViewDto> findByName(@Param("hotelFreebiesName") String hotelFreebiesName,
                                          @Param("offset") long offset,
                                          @Param("size") int size);

    // 개수 조회 (검색어 없으면 전체 개수)
    long countByName(@Param("hotelFreebiesName") String hotelFreebiesName);
}