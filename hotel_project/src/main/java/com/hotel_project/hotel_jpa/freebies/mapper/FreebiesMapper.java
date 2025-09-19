package com.hotel_project.hotel_jpa.freebies.mapper;

import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Mapper
public interface FreebiesMapper {
    FreebiesDto findById(Long id);
    List<FreebiesDto> findAll();
    List<FreebiesDto> findByNameContains(String freebiesName, Pageable pageable);
}
