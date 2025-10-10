package com.hotel_project.hotel_jpa.city.mapper;

import com.hotel_project.hotel_jpa.city.dto.CityHotelOneDto;
import com.hotel_project.hotel_jpa.city.dto.CityViewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CityMapper {

    // 이름 검색 통합 (페이지네이션, 검색어 없으면 전체 조회) - ViewDto 반환
    List<CityViewDto> findByName(@Param("cityName") String cityName,
                                 @Param("offset") long offset,
                                 @Param("size") int size);

    // 개수 조회 (검색어 없으면 전체 개수)
    long countByName(@Param("cityName") String cityName);

    // 추천 도시 목록 조회 (이미지와 최저가 포함)
    List<CityHotelOneDto> findFeaturedCities(@Param("limit") int limit);
}
