package com.hotel_project.hotel_jpa.freebies.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesEntity;
import com.hotel_project.hotel_jpa.freebies.mapper.FreebiesMapper;
import com.hotel_project.hotel_jpa.freebies.repository.FreebiesRepository;
import lombok.RequiredArgsConstructor;
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

    // SELECT - MyBatis 사용
    public List<FreebiesDto> findAll() {
        return freebiesMapper.findAll();
    }

    public FreebiesDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        FreebiesDto freebiesDto = freebiesMapper.findById(id);
        if (freebiesDto == null) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        return freebiesDto;
    }

    public List<FreebiesDto> findByName(String freebiesName) throws CommonExceptionTemplate {
        if (freebiesName == null || freebiesName.trim().isEmpty()) {
            throw MemberException.INVALID_DATA.getException();
        }

        return freebiesMapper.findByName(freebiesName);
    }

    // INSERT - JPA 사용
    public String insert(FreebiesDto freebiesDto) throws CommonExceptionTemplate {
        if (freebiesDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        // 중복 체크
        if (freebiesDto.getFreebiesName() != null &&
                freebiesRepository.existsByFreebiesName(freebiesDto.getFreebiesName().trim())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        // 인터페이스 메서드 활용
        FreebiesEntity entity = new FreebiesEntity();
        entity.copyMembers(freebiesDto);

        freebiesRepository.save(entity);
        return "insert ok";
    }

    // UPDATE
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

        // 중복 체크 (이름이 변경될 때만)
        if (freebiesDto.getFreebiesName() != null &&
                !freebiesDto.getFreebiesName().equals(entity.getFreebiesName()) &&
                freebiesRepository.existsByFreebiesNameAndIdNot(freebiesDto.getFreebiesName(), freebiesDto.getId())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        // 인터페이스 메서드 활용 (null이 아닌 필드만 복사)
        entity.copyNotNullMembers(freebiesDto);  // 인터페이스 메서드 사용

        freebiesRepository.save(entity);
        return "update ok";
    }

    // DELETE
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