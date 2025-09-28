package com.hotel_project.hotel_jpa.room.mapper;

import com.hotel_project.hotel_jpa.room.dto.RoomViewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomMapper {

    List<RoomViewDto> findByName(@Param("RoomName") String roomName,
                                 @Param("offset") long offset,
                                 @Param("size") int size);

    long countByName(@Param("RoomName") String roomName);
}
