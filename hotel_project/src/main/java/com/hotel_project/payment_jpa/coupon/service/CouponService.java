package com.hotel_project.payment_jpa.coupon.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesDto;
import com.hotel_project.hotel_jpa.amenities.dto.AmenitiesEntity;
import com.hotel_project.hotel_jpa.amenities.mapper.AmenitiesMapper;
import com.hotel_project.hotel_jpa.amenities.repository.AmenitiesRepository;
import com.hotel_project.payment_jpa.coupon.dto.CouponDto;
import com.hotel_project.payment_jpa.coupon.dto.CouponEntity;
import com.hotel_project.payment_jpa.coupon.mapper.CouponMapper;
import com.hotel_project.payment_jpa.coupon.repository.CouponRepository;
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
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponMapper couponMapper;

    // 이름 검색으로 통합 (페이지네이션 포함, 검색어 없으면 전체 조회)
    public Page<CouponDto> findByName(Pageable pageable, String couponName) {
        List<CouponDto> content = couponMapper.findByName(couponName, pageable.getOffset(), pageable.getPageSize());
        long total = couponMapper.countByName(couponName);
        return new PageImpl<>(content, pageable, total);
    }

    // Repository로 변경
    public CouponDto findById(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<CouponEntity> entityOptional = couponRepository.findById(id);
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        CouponEntity entity = entityOptional.get();
        CouponDto dto = new CouponDto();
        dto.copyMembers(entity);
        return dto;
    }

    // INSERT - JPA 사용
    public String insert(CouponDto couponDto) throws CommonExceptionTemplate {
        if (couponDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        if (couponDto.getCouponName() != null &&
                couponRepository.existsByCouponName(couponDto.getCouponName().trim())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        CouponEntity entity = new CouponEntity();
        entity.copyMembers(couponDto);
        couponRepository.save(entity);
        return "insert ok";
    }

    // UPDATE - JPA 사용
    public String update(CouponDto couponDto) throws CommonExceptionTemplate {
        if (couponDto == null) {
            throw MemberException.INVALID_DATA.getException();
        }
        if (couponDto.getId() == null) {
            throw MemberException.INVALID_ID.getException();
        }

        Optional<CouponEntity> entityOptional = couponRepository.findById(couponDto.getId());
        if (!entityOptional.isPresent()) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        CouponEntity entity = entityOptional.get();

        if (couponDto.getCouponName() != null &&
                !couponDto.getCouponName().equals(entity.getCouponName()) &&
                couponRepository.existsByCouponNameAndIdNot(couponDto.getCouponName(), couponDto.getId())) {
            throw MemberException.DUPLICATE_DATA.getException();
        }

        entity.copyNotNullMembers(couponDto);
        couponRepository.save(entity);
        return "update ok";
    }

    // DELETE - JPA 사용
    public String delete(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }
        if (!couponRepository.existsById(id)) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }
        couponRepository.deleteById(id);
        return "delete ok";
    }
}