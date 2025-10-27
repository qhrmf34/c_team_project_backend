package com.hotel_project.payment_jpa.refund_reason.repository;

import com.hotel_project.payment_jpa.refund_reason.dto.RefundReasonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundReasonRepository extends JpaRepository<RefundReasonEntity, Long> {

    /**
     * 특정 결제 ID로 환불 사유 조회
     */
    Optional<RefundReasonEntity> findByPaymentId(Long paymentId);

    /**
     * 특정 결제 ID의 모든 환불 사유 조회 (다중 환불이 가능한 경우)
     */
    List<RefundReasonEntity> findAllByPaymentId(Long paymentId);
}