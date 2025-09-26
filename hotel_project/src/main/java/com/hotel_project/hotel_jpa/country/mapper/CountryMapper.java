package com.hotel_project.hotel_jpa.country.mapper;

import com.hotel_project.hotel_jpa.country.dto.CountryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CountryMapper {

    // 전체 조회 (페이지네이션)
    List<CountryDto> findAll(@Param("search") String search,
                             @Param("offset") long offset,
                             @Param("size") int size);

    // 전체 개수 조회
    long countAll(@Param("search") String search);

    // ID로 단건 조회
    CountryDto findById(@Param("id") Long id);

    // 이름으로 검색 (LIKE 검색)
    List<CountryDto> findByName(@Param("countryName") String countryName);
}