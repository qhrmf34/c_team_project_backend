package com.hotel_project.hotel_jpa.hotel.mapper;

import com.hotel_project.hotel_jpa.hotel.dto.HotelViewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HotelMapper {

    List<HotelViewDto> findByName(@Param("HotelName") String hotelName,
                                  @Param("offset") long offset,
                                  @Param("size") int size);

    long countByName(@Param("HotelName") String hotelName);
}
