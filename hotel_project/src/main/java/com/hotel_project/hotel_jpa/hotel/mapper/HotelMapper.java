package com.hotel_project.hotel_jpa.hotel.mapper;

import com.hotel_project.hotel_jpa.hotel.dto.HotelViewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface HotelMapper {

    List<HotelViewDto> findByName(@Param("HotelName") String hotelName,
                                  @Param("offset") long offset,
                                  @Param("size") int size);

    long countByName(@Param("HotelName") String hotelName);
    /**
     * ✅ 호텔 기본 정보 조회 (국가, 도시 포함)
     */
    Map<String, Object> getHotelBasicInfo(@Param("hotelId") Long hotelId);

    /**
     * ✅ 호텔 첫 번째 이미지 조회
     */
    String getFirstHotelImage(@Param("hotelId") Long hotelId);
}
