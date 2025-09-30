package com.hotel_project.hotel_jpa.room_pricing.mapper;

import com.hotel_project.hotel_jpa.room_pricing.dto.RoomPricingViewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomPricingMapper {

    List<RoomPricingViewDto> findByName(@Param("RoomName") String roomName,
                                        @Param("offset") long offset,
                                        @Param("size") int size);

    long countByName(@Param("RoomName") String roomName);
}