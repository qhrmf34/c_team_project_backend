package com.hotel_project.payment_jpa.refund_reason.dto;

import com.hotel_project.common_jpa.dto.IId;
import java.time.LocalDateTime;

public interface IRefundReason extends IId {
    Long getId();
    void setId(Long id);

    Long getPaymentId();
    void setPaymentId(Long paymentId);

    RefundReasonType getMainReason();
    void setMainReason(RefundReasonType mainReason);

    String getDetailReason();
    void setDetailReason(String detailReason);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    default void copyMembers(IRefundReason iRefundReason) {
        if (iRefundReason == null) {
            return;
        }
        setId(iRefundReason.getId());
        setPaymentId(iRefundReason.getPaymentId());
        setMainReason(iRefundReason.getMainReason());
        setDetailReason(iRefundReason.getDetailReason());
        setCreatedAt(iRefundReason.getCreatedAt());
    }

    default void copyNotNullMembers(IRefundReason iRefundReason) {
        if (iRefundReason == null) {
            return;
        }
        if (iRefundReason.getId() != null) { setId(iRefundReason.getId()); }
        if (iRefundReason.getPaymentId() != null) { setPaymentId(iRefundReason.getPaymentId()); }
        if (iRefundReason.getMainReason() != null) { setMainReason(iRefundReason.getMainReason()); }
        if (iRefundReason.getDetailReason() != null) { setDetailReason(iRefundReason.getDetailReason()); }
        if (iRefundReason.getCreatedAt() != null) { setCreatedAt(iRefundReason.getCreatedAt()); }
    }
}