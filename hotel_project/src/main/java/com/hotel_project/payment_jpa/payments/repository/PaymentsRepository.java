// PaymentsRepository.java
package com.hotel_project.payment_jpa.payments.repository;

import com.hotel_project.payment_jpa.payments.dto.PaymentsEntity;
import com.hotel_project.payment_jpa.payments.dto.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentsRepository extends JpaRepository<PaymentsEntity, Long> {

    // 토스 결제 키로 결제 조회
    Optional<PaymentsEntity> findByTossPaymentKey(String tossPaymentKey);

    // 토스 결제 키 존재 여부 확인
    boolean existsByTossPaymentKey(String tossPaymentKey);
}