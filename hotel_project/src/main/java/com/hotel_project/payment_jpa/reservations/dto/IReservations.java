package com.hotel_project.payment_jpa.reservations.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hotel_project.common_jpa.dto.IId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface IReservations extends IId {
    Long getId();
    void setId(Long id);

    IId getMember();
    void setMember(IId member);

    @JsonIgnore
    Long getMemberId();
    void setMemberId(Long memberId);

    IId getRoom();
    void setRoom(IId room);

    @JsonIgnore
    Long getRoomId();
    void setRoomId(Long roomId);

    LocalDate getCheckInDate();
    void setCheckInDate(LocalDate checkInDate);

    LocalDate getCheckOutDate();
    void setCheckOutDate(LocalDate checkOutDate);

    Integer getGuestsCount();
    void setGuestsCount(Integer guestsCount);

    BigDecimal getBasePayment();
    void setBasePayment(BigDecimal basePayment);

    Boolean getReservationsStatus();
    void setReservationsStatus(Boolean reservationsStatus);

    LocalDateTime getReservationsAt();
    void setReservationsAt(LocalDateTime reservationsAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    default void copyMembers(IReservations iReservations) {
        if (iReservations == null) {
            return;
        }
        setId(iReservations.getId());
        setMember(iReservations.getMember());
        setRoom(iReservations.getRoom());
        setCheckInDate(iReservations.getCheckInDate());
        setCheckOutDate(iReservations.getCheckOutDate());
        setGuestsCount(iReservations.getGuestsCount());
        setBasePayment(iReservations.getBasePayment());
        setReservationsStatus(iReservations.getReservationsStatus());
        setReservationsAt(iReservations.getReservationsAt());
        setUpdatedAt(iReservations.getUpdatedAt());
    }

    default void copyNotNullMembers(IReservations iReservations) {
        if (iReservations == null) {
            return;
        }
        if (iReservations.getId() != null) {
            setId(iReservations.getId());
        }
        if (iReservations.getMember() != null) {
            setMember(iReservations.getMember());
        }
        if (iReservations.getRoom() != null) {
            setRoom(iReservations.getRoom());
        }
        if (iReservations.getCheckInDate() != null) {
            setCheckInDate(iReservations.getCheckInDate());
        }
        if (iReservations.getCheckOutDate() != null) {
            setCheckOutDate(iReservations.getCheckOutDate());
        }
        if (iReservations.getGuestsCount() != null) {
            setGuestsCount(iReservations.getGuestsCount());  // 오타 수정
        }
        if (iReservations.getBasePayment() != null) {
            setBasePayment(iReservations.getBasePayment());
        }
        if (iReservations.getReservationsStatus() != null) {
            setReservationsStatus(iReservations.getReservationsStatus());
        }
        if (iReservations.getReservationsAt() != null) {
            setReservationsAt(iReservations.getReservationsAt());  // 메서드명 수정
        }
        if (iReservations.getUpdatedAt() != null) {
            setUpdatedAt(iReservations.getUpdatedAt());
        }
    }
}