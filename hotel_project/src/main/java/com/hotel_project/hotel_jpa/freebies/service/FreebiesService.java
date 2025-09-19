package com.hotel_project.hotel_jpa.freebies.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesDto;
import com.hotel_project.hotel_jpa.freebies.dto.FreebiesEntity;
import com.hotel_project.hotel_jpa.freebies.dto.IFreebies;
import com.hotel_project.hotel_jpa.freebies.mapper.FreebiesMapper;
import com.hotel_project.hotel_jpa.freebies.repository.FreebiesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class FreebiesService {

    @Autowired
    private FreebiesRepository freebiesRepository; // JPA - CUD

    @Autowired
    private FreebiesMapper freebiesMapper; // MyBatis - R

    // CREATE - JPA 사용
    public IFreebies insertRepository(FreebiesDto dto) throws CommonExceptionTemplate {
        try {
            FreebiesEntity entity = new FreebiesEntity();
            entity.copyMembers(dto); // IFreebies 인터페이스의 default 메서드 사용
            FreebiesEntity result = this.freebiesRepository.save(entity);
            log.info("Freebies created with ID: {}", result.getId());
            return result;
        } catch (Exception e) {
            log.error("Error creating freebies: {}", e.getMessage());
            throw MemberException.NOT_EXIST_MEMBERID.getException(); // 적절한 예외로 변경 필요
        }
    }

    // UPDATE - JPA 사용
    public IFreebies updateRepository(Long id, FreebiesDto dto) throws CommonExceptionTemplate {
        try {
            Optional<FreebiesEntity> existingEntity = this.freebiesRepository.findById(id);
            if (!existingEntity.isPresent()) {
                throw MemberException.NOT_EXIST_MEMBERID2.getException();
            }

            FreebiesEntity entity = existingEntity.get();
            entity.copyNotNullMembers(dto); // IFreebies 인터페이스의 default 메서드 사용
            FreebiesEntity result = this.freebiesRepository.save(entity);
            log.info("Freebies updated with ID: {}", result.getId());
            return result;
        } catch (Exception e) {
            log.error("Error updating freebies: {}", e.getMessage());
            throw MemberException.NOT_EXIST_MEMBERID3.getException();
        }
    }

    // DELETE - JPA 사용
    public Boolean deleteRepository(Long id) throws CommonExceptionTemplate {
        try {
            if (!this.freebiesRepository.existsById(id)) {
                throw MemberException.NOT_EXIST_MEMBERID4.getException();
            }
            this.freebiesRepository.deleteById(id);
            log.info("Freebies deleted with ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("Error deleting freebies: {}", e.getMessage());
            throw MemberException.NOT_EXIST_MEMBERID5.getException();
        }
    }

    // READ - MyBatis 사용
    @Transactional(readOnly = true)
    public IFreebies findByIdMybatis(Long id) throws CommonExceptionTemplate {
        try {
            FreebiesDto find = this.freebiesMapper.findById(id);
            if (find == null) {
                throw MemberException.NOT_EXIST_MEMBERID6.getException();
            }
            log.info("Freebies found with ID: {}", id);
            return find;
        } catch (Exception e) {
            log.error("Error finding freebies by ID: {}", e.getMessage());
            throw MemberException.NOT_EXIST_MEMBERID6.getException();
        }
    }

    // READ ALL - MyBatis 사용
    @Transactional(readOnly = true)
    public List<IFreebies> findAllMybatis() {
        List<FreebiesDto> all = this.freebiesMapper.findAll();
        List<IFreebies> result = all.parallelStream()
                .map(x -> (IFreebies) x).toList();
        log.info("Found {} freebies", result.size());
        return result;
    }

    // SEARCH - MyBatis 사용 (Slice)
    @Transactional(readOnly = true)
    public Slice<FreebiesDto> findByNameContainsMybatis(String freebiesName, Pageable pageable) {
        List<FreebiesDto> list = this.freebiesMapper.findByNameContains(freebiesName, pageable);

        // Slice는 다음 페이지 존재 여부만 확인 (총 개수 불필요)
        boolean hasNext = list.size() > pageable.getPageSize();
        if (hasNext) {
            list = list.subList(0, pageable.getPageSize()); // 실제 페이지 크기만큼만 자르기
        }

        Slice<FreebiesDto> result = new SliceImpl<>(list, pageable, hasNext);
        log.info("Search found {} freebies for name containing: {}", list.size(), freebiesName);
        return result;
    }
}