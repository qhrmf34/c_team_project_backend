package com.hotel_project.payment_jpa.ticket.dto;

import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.payment_jpa.payments.dto.PaymentsEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket_tbl")
public class TicketEntity implements ITicket{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "payments_id", nullable = false)
    private PaymentsEntity paymentsEntity;

    @Transient
    private Long paymentId;

    @Column(nullable = false)
    private String ticketImageName;

    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Boolean isUsed = false;

    @Column(nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Override
    public IId getPayments(){
        return this.paymentsEntity;
    }

    @Override
    public void setPayments(IId payments) {
        if (payments == null) {
            return;
        }
        if (this.paymentsEntity == null) {
            this.paymentsEntity = new PaymentsEntity();
        }
        this.paymentsEntity.copyMembersId(payments);
    }

    @Override
    public Long getPaymentId() {
        return this.paymentsEntity != null ? this.paymentsEntity.getId() : null;
    }

    @Override
    public void setPaymentId(Long paymentId) {
        if (paymentId == null) {
            throw new IllegalArgumentException("paymentId cannot be null");
        }
        if (this.paymentsEntity == null) {
            this.paymentsEntity = new PaymentsEntity();
        }
        this.paymentsEntity.setId(paymentId);
        this.paymentId = paymentId;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
    }
}
