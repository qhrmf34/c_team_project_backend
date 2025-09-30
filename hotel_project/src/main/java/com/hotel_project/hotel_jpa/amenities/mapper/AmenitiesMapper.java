package com.hotel_project.hotel_jpa.amenities.mapper;

import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AmenitiesMapper {

    // 이름 검색 통합 (페이지네이션, 검색어 없으면 전체 조회)
    List<AmenitiesDto> findByName(@Param("amenitiesName") String countryName,
                                  @Param("offset") long offset,
                                  @Param("size") int size);

    // 개수 조회 (검색어 없으면 전체 개수)
    long countByName(@Param("amenitiesName") String countryName);
}
