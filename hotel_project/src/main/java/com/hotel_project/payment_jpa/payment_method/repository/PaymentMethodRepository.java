package com.hotel_project.payment_jpa.payment_method.repository;

import com.hotel_project.payment_jpa.payment_method.dto.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, Long> {

    // 토스키 중복 체크
    boolean existsByTossKey(String tossKey);

    // 회원별 결제수단 존재 여부 확인
    boolean existsByMemberEntityId(Long memberId);
}

