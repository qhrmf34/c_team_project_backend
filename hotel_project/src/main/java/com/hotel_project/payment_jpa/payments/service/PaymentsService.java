// PaymentsService.java
package com.hotel_project.payment_jpa.payments.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.payment_jpa.payment_method.dto.PaymentMethodDto;
import com.hotel_project.payment_jpa.payments.dto.*;
import com.hotel_project.payment_jpa.payments.mapper.PaymentsMapper;
import com.hotel_project.payment_jpa.payments.repository.PaymentsRepository;
import com.hotel_project.payment_jpa.payment_method.service.TossPaymentService;
import com.hotel_project.payment_jpa.payment_method.mapper.PaymentMethodMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private final PaymentsMapper paymentsMapper;
    private final PaymentMethodMapper paymentMethodMapper;
    private final TossPaymentService tossPaymentService;

    /**
     * 결제 처리
     */
    public PaymentsDto processPayment(PaymentRequestDto request) throws CommonExceptionTemplate {
        if (request == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        try {
            log.info("결제 처리 시작 - 예약 ID: {}, 금액: {}", request.getReservationsId(), request.getPaymentAmount());

            // 1. 결제수단 조회
            PaymentMethodDto paymentMethod = paymentMethodMapper.findById(request.getPaymentMethodId());
            if (paymentMethod == null) {
                throw MemberException.NOT_EXIST_DATA.getException();
            }

            // 2. 토스 결제 처리
            TossPaymentResponseDto tossResponse = tossPaymentService.processPayment(
                    paymentMethod.getTossKey(), request
            );

            // 3. 결제 엔티티 생성 및 저장 (ID만 설정)
            PaymentsEntity paymentsEntity = new PaymentsEntity();
            paymentsEntity.setReservationsId(request.getReservationsId());
            paymentsEntity.setPaymentMethodId(request.getPaymentMethodId());
            if (request.getCouponId() != null) {
                paymentsEntity.setCouponId(request.getCouponId());
            }
            paymentsEntity.setPaymentAmount(request.getPaymentAmount());
            paymentsEntity.setPaymentDate(LocalDateTime.now());
            paymentsEntity.setPaymentStatus(PaymentStatus.paid);
            paymentsEntity.setTossPaymentKey(tossResponse.getPaymentKey());
            paymentsEntity.setRefundAmount(BigDecimal.ZERO);
            paymentsEntity.setUpdatedAt(LocalDateTime.now());

            PaymentsEntity savedEntity = paymentsRepository.save(paymentsEntity);
            log.info("결제 완료 - 결제 ID: {}, 토스 결제 키: {}", savedEntity.getId(), savedEntity.getTossPaymentKey());

            // 4. DTO 변환 후 반환
            PaymentsDto result = new PaymentsDto();
            result.setId(savedEntity.getId());
            result.setReservationsId(savedEntity.getReservationsId());
            result.setPaymentMethodId(savedEntity.getPaymentMethodId());
            result.setCouponId(savedEntity.getCouponId());
            result.setPaymentAmount(savedEntity.getPaymentAmount());
            result.setPaymentDate(savedEntity.getPaymentDate());
            result.setPaymentStatus(savedEntity.getPaymentStatus());
            result.setTossPaymentKey(savedEntity.getTossPaymentKey());
            result.setRefundAmount(savedEntity.getRefundAmount());
            result.setRefundDate(savedEntity.getRefundDate());
            result.setUpdatedAt(savedEntity.getUpdatedAt());

            return result;

        } catch (Exception e) {
            log.error("결제 처리 실패 - 예약 ID: {}", request.getReservationsId(), e);

            // 실패한 결제 기록 저장
            try {
                PaymentsEntity failedPayment = new PaymentsEntity();
                failedPayment.setReservationsId(request.getReservationsId());
                failedPayment.setPaymentMethodId(request.getPaymentMethodId());
                if (request.getCouponId() != null) {
                    failedPayment.setCouponId(request.getCouponId());
                }
                failedPayment.setPaymentAmount(request.getPaymentAmount());
                failedPayment.setPaymentDate(LocalDateTime.now());
                failedPayment.setPaymentStatus(PaymentStatus.failed);
                failedPayment.setRefundAmount(BigDecimal.ZERO);
                failedPayment.setUpdatedAt(LocalDateTime.now());

                paymentsRepository.save(failedPayment);
            } catch (Exception saveError) {
                log.error("실패한 결제 기록 저장 중 오류", saveError);
            }

            throw new CommonExceptionTemplate(500, "결제 처리에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 예약별 결제 내역 조회 (MyBatis 사용)
     */
    @Transactional(readOnly = true)
    public List<PaymentsDto> getPaymentsByReservation(Long reservationsId) throws CommonExceptionTemplate {
        if (reservationsId == null) {
            throw MemberException.INVALID_ID.getException();
        }

        return paymentsMapper.findByReservationsId(reservationsId);
    }

    /**
     * 회원별 결제 내역 조회 (MyBatis 사용)
     */
    @Transactional(readOnly = true)
    public List<PaymentsDto> getPaymentsByMember(Long memberId) throws CommonExceptionTemplate {
        if (memberId == null) {
            throw MemberException.INVALID_ID.getException();
        }

        return paymentsMapper.findByMemberId(memberId);
    }

    /**
     * 결제 단건 조회 (MyBatis 사용)
     */
    @Transactional(readOnly = true)
    public PaymentsDto getPayment(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        PaymentsDto result = paymentsMapper.findById(id);
        if (result == null) {
            throw MemberException.NOT_EXIST_DATA.getException();
        }

        return result;
    }
}