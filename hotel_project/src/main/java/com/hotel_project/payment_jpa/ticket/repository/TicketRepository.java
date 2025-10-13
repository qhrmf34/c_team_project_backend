package com.hotel_project.payment_jpa.ticket.repository;

import com.hotel_project.payment_jpa.ticket.dto.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<TicketEntity, Long> {
}