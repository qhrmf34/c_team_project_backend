package com.hotel_project.hotel_jpa.freebies.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesEntity;
import com.hotel_project.hotel_jpa.freebies.mapper.FreebiesMapper;
import com.hotel_project.hotel_jpa.freebies.repository.FreebiesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FreebiesService {

    private final FreebiesRepository freebiesRepository;
    private final FreebiesMapper freebiesMapper;

    // 이름 검색으로 통합 (페이지네이션 포함, 검색어 없으면 전체 조회)
    public Page<FreebiesDto> findByName(Pageable pageable, String freebiesName) {
        List<FreebiesDto> content = freebiesMapper.findByName(freebiesName, pageable.getOffset(), pageable.getPageSize());
        long total = freebiesMapper.countByName(freebiesName);
        return new PageImpl<>(content, pageable, total);
    }

    // Repository로 변경
    public FreebiesDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<FreebiesEntity> entityOptional = freebiesRepository.findById(id);
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        FreebiesEntity entity = entityOptional.get();
        FreebiesDto dto = new FreebiesDto();
        dto.copyMembers(entity);
        return dto;
    }

    // INSERT - JPA 사용
    public String insert(FreebiesDto freebiesDto) throws CommonExceptionTemplate {
        if (freebiesDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        if (freebiesDto.getFreebiesName() != null &&
                freebiesRepository.existsByFreebiesName(freebiesDto.getFreebiesName().trim())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        FreebiesEntity entity = new FreebiesEntity();
        entity.copyMembers(freebiesDto);
        freebiesRepository.save(entity);
        return "insert ok";
    }

    // UPDATE - JPA 사용
    public String update(FreebiesDto freebiesDto) throws CommonExceptionTemplate {
        if (freebiesDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }
        if (freebiesDto.getId() == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<FreebiesEntity> entityOptional = freebiesRepository.findById(freebiesDto.getId());
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        FreebiesEntity entity = entityOptional.get();

        if (freebiesDto.getFreebiesName() != null &&
                !freebiesDto.getFreebiesName().equals(entity.getFreebiesName()) &&
                freebiesRepository.existsByFreebiesNameAndIdNot(freebiesDto.getFreebiesName(), freebiesDto.getId())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        entity.copyNotNullMembers(freebiesDto);
        freebiesRepository.save(entity);
        return "update ok";
    }

    // DELETE - JPA 사용
    public String delete(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }
        if (!freebiesRepository.existsById(id)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }
        freebiesRepository.deleteById(id);
        return "delete ok";
    }
}