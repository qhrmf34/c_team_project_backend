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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payments_id", nullable = false)
    private PaymentsEntity paymentsEntity;

    @Transient
    private Long paymentId;

    @Column(nullable = false)
    private String ticketImageName;

    @Column(unique = true, length = 50)
    private String barcode;

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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        // ✅ 바코드 자동 생성
        if (barcode == null) {
            barcode = generateBarcode();
        }
    }

    // ✅ 바코드 생성 메서드
    private String generateBarcode() {
        // 현재 시간 + 랜덤 숫자로 유니크한 바코드 생성
        return String.format("TKT%d%04d",
                System.currentTimeMillis() / 1000,
                (int)(Math.random() * 10000));
    }
}
