// PaymentMethodService.java
package com.hotel_project.payment_jpa.payment_method.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.member_jpa.member.dto.MemberEntity;
import com.hotel_project.member_jpa.member.repository.MemberRepository;
import com.hotel_project.payment_jpa.payment_method.dto.*;
import com.hotel_project.payment_jpa.payment_method.mapper.PaymentMethodMapper;
import com.hotel_project.payment_jpa.payment_method.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;
    private final TossPaymentService tossPaymentService;
    private final MemberRepository memberRepository;

    /**
     * 결제수단 등록 (토스 빌링키 발급 및 저장)
     */
    public PaymentMethodDto registerPaymentMethod(CardRegistrationRequestDto request, Long memberId) throws CommonExceptionTemplate {
        if (request == null || memberId == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        try {
            log.info("회원 ID: {}의 결제수단 등록 시작", memberId);

            // 1. 회원 존재 여부 확인 (엔티티 조회하지 않고 존재만 확인)
            if (!memberRepository.existsById(memberId)) {
                throw MemberException.NOT_EXIST_DATA.getException();
            }

            // 2. 토스에 빌링키 등록 요청
            TossBillingResponseDto tossResponse = tossPaymentService.registerBillingKey(request, memberId);

            // 3. 빌링키 중복 체크
            if (paymentMethodRepository.existsByTossKey(tossResponse.getBillingKey())) {
                throw MemberException.DUPLICATE_DATA.getException();
            }

            // 4. 결제수단 엔티티 생성 (회원 ID만 설정)
            PaymentMethodEntity entity = new PaymentMethodEntity();
            entity.setMemberId(memberId); // ID만 설정하여 detached 문제 방지
            entity.setTossKey(tossResponse.getBillingKey());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            PaymentMethodEntity savedEntity = paymentMethodRepository.save(entity);
            log.info("결제수단 등록 완료 - ID: {}, 토스키: {}", savedEntity.getId(), savedEntity.getTossKey());

            // 5. DTO 변환 후 반환
            PaymentMethodDto result = new PaymentMethodDto();
            result.setId(savedEntity.getId());
            result.setMemberId(savedEntity.getMemberId());
            result.setTossKey(savedEntity.getTossKey());
            result.setCreatedAt(savedEntity.getCreatedAt());
            result.setUpdatedAt(savedEntity.getUpdatedAt());

            return result;

        } catch (Exception e) {
            log.error("회원 ID: {}의 결제수단 등록 실패", memberId, e);
            throw new CommonExceptionTemplate(500, "결제수단 등록에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 회원의 결제수단 목록 조회 (MyBatis 사용)
     */
    @Transactional(readOnly = true)
    public List<PaymentMethodDto> getPaymentMethods(Long memberId) throws CommonExceptionTemplate {
        if (memberId == null) {
            throw MemberException.INVALID_ID.getException();
        }

        return paymentMethodMapper.findByMemberId(memberId);
    }

    /**
     * 결제수단 단건 조회 (MyBatis 사용)
     */
    @Transactional(readOnly = true)
    public PaymentMethodDto getPaymentMethod(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        PaymentMethodDto result = paymentMethodMapper.findById(id);
        if (result == null) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        return result;
    }

    /**
     * 결제수단 삭제 (JPA 사용)
     */
    public String deletePaymentMethod(Long id, Long memberId) throws CommonExceptionTemplate {
        if (id == null || memberId == null) {
            throw MemberException.INVALID_ID.getException();
        }

        PaymentMethodEntity entity = paymentMethodRepository.findById(id)
                .orElseThrow(() -> MemberException.NOT_EXIST_DATA.getException());

        if (!entity.getMemberId().equals(memberId)) {
            throw new CommonExceptionTemplate(403, "권한이 없습니다");
        }

        paymentMethodRepository.delete(entity);
        log.info("결제수단 삭제 완료 - ID: {}", id);

        return "delete ok";
    }
}