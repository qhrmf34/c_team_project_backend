package com.hotel_project.payment_jpa.ticket.repository;

import com.hotel_project.payment_jpa.ticket.dto.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {

    // ✅ 결제 ID로 티켓 조회
    Optional<TicketEntity> findByPaymentsEntity_Id(Long paymentId);

    // ✅ 바코드로 티켓 조회
    Optional<TicketEntity> findByBarcode(String barcode);
}