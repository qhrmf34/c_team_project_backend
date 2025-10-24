package com.hotel_project.payment_jpa.refund_reason.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.payment_jpa.refund_reason.dto.RefundReasonDto;
import com.hotel_project.payment_jpa.refund_reason.dto.RefundReasonEntity;
import com.hotel_project.payment_jpa.refund_reason.repository.RefundReasonRepository;
import com.hotel_project.payment_jpa.payments.repository.PaymentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefundReasonService {

    private final RefundReasonRepository refundReasonRepository;
    private final PaymentsRepository paymentsRepository;

    /**
     * 환불 사유 저장
     */
    @Transactional
    public RefundReasonDto saveRefundReason(RefundReasonDto refundReasonDto) throws CommonExceptionTemplate {
        // 결제 ID 유효성 검증
        if (!paymentsRepository.existsById(refundReasonDto.getPaymentId())) {
            throw new CommonExceptionTemplate(404, "존재하지 않는 결제 ID입니다.");
        }

        RefundReasonEntity entity = new RefundReasonEntity();
        entity.copyMembers(refundReasonDto);

        RefundReasonEntity saved = refundReasonRepository.save(entity);

        RefundReasonDto result = new RefundReasonDto();
        result.copyMembers(saved);

        log.info("환불 사유 저장 완료 - Payment ID: {}, Main Reason: {}",
                saved.getPaymentId(), saved.getMainReason());

        return result;
    }

    /**
     * 특정 결제의 환불 사유 조회
     */
    @Transactional(readOnly = true)
    public RefundReasonDto getRefundReasonByPaymentId(Long paymentId) throws CommonExceptionTemplate {
        RefundReasonEntity entity = refundReasonRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "해당 결제의 환불 사유를 찾을 수 없습니다."));

        RefundReasonDto dto = new RefundReasonDto();
        dto.copyMembers(entity);
        return dto;
    }

    /**
     * 특정 결제의 모든 환불 사유 조회 (다중 환불인 경우)
     */
    @Transactional(readOnly = true)
    public List<RefundReasonDto> getAllRefundReasonsByPaymentId(Long paymentId) {
        List<RefundReasonEntity> entities = refundReasonRepository.findAllByPaymentId(paymentId);

        return entities.stream()
                .map(entity -> {
                    RefundReasonDto dto = new RefundReasonDto();
                    dto.copyMembers(entity);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 환불 사유 ID로 조회
     */
    @Transactional(readOnly = true)
    public RefundReasonDto getRefundReasonById(Long id) throws CommonExceptionTemplate {
        RefundReasonEntity entity = refundReasonRepository.findById(id)
                .orElseThrow(() -> new CommonExceptionTemplate(404, "환불 사유를 찾을 수 없습니다."));

        RefundReasonDto dto = new RefundReasonDto();
        dto.copyMembers(entity);
        return dto;
    }

    /**
     * 모든 환불 사유 조회
     */
    @Transactional(readOnly = true)
    public List<RefundReasonDto> getAllRefundReasons() {
        List<RefundReasonEntity> entities = refundReasonRepository.findAll();

        return entities.stream()
                .map(entity -> {
                    RefundReasonDto dto = new RefundReasonDto();
                    dto.copyMembers(entity);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}