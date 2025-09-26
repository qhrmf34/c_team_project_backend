package com.hotel_project.hotel_jpa.city_image.mapper;

import com.hotel_project.hotel_jpa.city_image.dto.CityImageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CityImageMapper {

    // 전체 조회 (페이지네이션)
    List<CityImageDto> findAll(@Param("search") String search,
                               @Param("offset") long offset,
                               @Param("size") int size);

    // 전체 개수 조회
    long countAll(@Param("search") String search);

    // ID로 단건 조회
    CityImageDto findById(@Param("id") Long id);

    // 도시 이름으로 검색 (LIKE 검색)
    List<CityImageDto> findByName(@Param("cityName") String cityName);
}