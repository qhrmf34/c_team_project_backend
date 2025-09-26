package com.hotel_project.hotel_jpa.city.mapper;

import com.hotel_project.hotel_jpa.city.dto.CityDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CityMapper {

    // 전체 조회 (페이지네이션)
    List<CityDto> findAll(@Param("search") String search,
                          @Param("offset") long offset,
                          @Param("size") int size);

    // 전체 개수 조회
    long countAll(@Param("search") String search);

    // ID로 단건 조회
    CityDto findById(@Param("id") Long id);

    // 이름으로 검색 (LIKE 검색)
    List<CityDto> findByName(@Param("cityName") String cityName);
}