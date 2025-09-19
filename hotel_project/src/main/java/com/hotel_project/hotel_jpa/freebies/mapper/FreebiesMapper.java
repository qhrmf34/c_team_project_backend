package com.hotel_project.hotel_jpa.freebies.mapper;

import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FreebiesMapper {

    // 전체 조회
    List<FreebiesDto> findAll();

    // ID로 단건 조회
    FreebiesDto findById(@Param("id") Long id);

    // 이름으로 검색 (LIKE 검색)
    List<FreebiesDto> findByName(@Param("freebiesName") String freebiesName);
}