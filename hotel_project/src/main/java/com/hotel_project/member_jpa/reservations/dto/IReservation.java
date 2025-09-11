package com.hotel_project.member_jpa.reservations.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IReservation {
    Long getId();
    void setId(Long id);

    Long getMemberId();
    void setMemberId(Long memberId);

    Long getRoomId();
    void setRoomId(Long roomId);

    LocalDate getCheckInDate();
    void setCheckInDate(LocalDate checkInDate);

    LocalDate getCheckOutDate();
    void setCheckOutDate(LocalDate checkOutDate);

    Integer getGuestCount();
    void setGuestCount(Integer guestCount);

    BigDecimal getBasePayment();
    void setBasePayment(BigDecimal basePayment);

    Boolean getReservationsStatus();
    void setReservationsStatus(Boolean reservationsStatus);

    LocalDateTime getReservationsDate();
    void setReservationsDate(LocalDateTime reservationsDate);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    /* insert용: 모든 값 복사 */
    default void copyReservation(IReservation src) {
        setId(src.getId());
        setMemberId(src.getMemberId());
        setRoomId(src.getRoomId());
        setCheckInDate(src.getCheckInDate());
        setCheckOutDate(src.getCheckOutDate());
        setGuestCount(src.getGuestCount());
        setBasePayment(src.getBasePayment());
        setReservationsStatus(src.getReservationsStatus());
        setReservationsDate(src.getReservationsDate());
        setUpdatedAt(src.getUpdatedAt());
    }

    /* update용: null 아닌 값만 복사 */
    default void copyNotNullReservation(IReservation src) {
        if (src.getId() != null) setId(src.getId());
        if (src.getMemberId() != null) setMemberId(src.getMemberId());
        if (src.getRoomId() != null) setRoomId(src.getRoomId());
        if (src.getCheckInDate() != null) setCheckInDate(src.getCheckInDate());
        if (src.getCheckOutDate() != null) setCheckOutDate(src.getCheckOutDate());
        if (src.getGuestCount() != null) setGuestCount(src.getGuestCount());
        if (src.getBasePayment() != null) setBasePayment(src.getBasePayment());
        if (src.getReservationsStatus() != null) setReservationsStatus(src.getReservationsStatus());
        if (src.getReservationsDate() != null) setReservationsDate(src.getReservationsDate());
        if (src.getUpdatedAt() != null) setUpdatedAt(src.getUpdatedAt());
    }
}

