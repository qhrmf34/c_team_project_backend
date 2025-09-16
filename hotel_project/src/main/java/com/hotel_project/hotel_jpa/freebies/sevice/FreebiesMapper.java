package com.hotel_project.hotel_jpa.freebies.sevice;

import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FreebiesMapper {
    void insert(FreebiesDto dto);
    List<FreebiesDto> findAll();
}
