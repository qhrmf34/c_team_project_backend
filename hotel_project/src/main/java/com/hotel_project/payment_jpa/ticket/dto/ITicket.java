package com.hotel_project.payment_jpa.ticket.dto;

import com.hotel_project.common_jpa.dto.IId;

import java.time.LocalDateTime;

public interface ITicket extends IId {
    Long getId();
    void setId(Long id);

    IId getPayments();
    void setPayments(IId payments);

    Long getPaymentId();
    void setPaymentId(Long paymentId);

    String getTicketImageName();
    void setTicketImageName(String ticketImageName);

    Boolean getIsUsed();
    void setIsUsed(Boolean isUsed);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime created);

    default void copyMembers(ITicket iTicket) {
        if (iTicket == null){
            return;
        }
        setId(iTicket.getId());
        setPayments(iTicket.getPayments());
        setTicketImageName(iTicket.getTicketImageName());
        setIsUsed(iTicket.getIsUsed());
        setCreatedAt(iTicket.getCreatedAt());
    }

    default void copyNotNullMembers(ITicket iTicket) {
        if (iTicket == null){
            return;
        }
        if (iTicket.getId() != null){ setId(iTicket.getId()); }
        if (iTicket.getPayments() != null){ setPayments(iTicket.getPayments()); }
        if (iTicket.getTicketImageName() != null){ setTicketImageName(iTicket.getTicketImageName()); }
        if (iTicket.getIsUsed() != null){ setIsUsed(iTicket.getIsUsed()); }
    }
}
