package com.hotel_project.payment_jpa.payments.repository;

import com.hotel_project.payment_jpa.payments.dto.PaymentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentsRepository extends JpaRepository<PaymentsEntity, Long> {
}