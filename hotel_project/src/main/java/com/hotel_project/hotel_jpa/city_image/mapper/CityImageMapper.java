package com.hotel_project.hotel_jpa.city_image.mapper;

import com.hotel_project.hotel_jpa.city_image.dto.CityImageViewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CityImageMapper {

    // 이름 검색 통합 (페이지네이션, 검색어 없으면 전체 조회) - ViewDto 반환
    List<CityImageViewDto> findByName(@Param("cityName") String cityName,
                                      @Param("offset") long offset,
                                      @Param("size") int size);

    // 개수 조회 (검색어 없으면 전체 개수)
    long countByName(@Param("cityName") String cityName);
}