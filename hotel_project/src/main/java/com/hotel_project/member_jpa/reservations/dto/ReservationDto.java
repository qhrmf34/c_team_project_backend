package com.hotel_project.member_jpa.reservations.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto implements IReservation {

    private Long id;

    @NotNull
    private Long memberId;

    @NotNull
    private Long roomId;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDate;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOutDate;

    @NotNull
    private Integer guestCount;

    @NotNull
    private BigDecimal basePayment;

    @NotNull
    private Boolean reservationsStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reservationsDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /** 회원/객실 요약 정보 (조회 응답 시만 채움) */
    private MemberInfo member;
    private RoomInfo room;

    /* ================== 비즈니스 편의 메서드 ================== */

    /** 숙박일수 계산 */
    public long getNights() {
        if (checkInDate != null && checkOutDate != null) {
            return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        }
        return 0;
    }

    /** 체크인까지 남은 일수 */
    public long getDaysUntilCheckIn() {
        if (checkInDate != null) {
            return ChronoUnit.DAYS.between(LocalDate.now(), checkInDate);
        }
        return 0;
    }

    /** 예약이 현재 유효한 상태인지 */
    public boolean isActive() {
        return Boolean.TRUE.equals(reservationsStatus);
    }

    /* ================== 변환 메서드 ================== */

    public static ReservationDto fromEntity(ReservationEntity e) {
        if (e == null) return null;
        return ReservationDto.builder()
                .id(e.getId())
                .memberId(e.getMemberId() != null ? e.getMemberId()
                        : (e.getMember() != null ? e.getMember().getId() : null))
                .roomId(e.getRoomId() != null ? e.getRoomId()
                        : (e.getRoom() != null ? e.getRoom().getId() : null))
                .checkInDate(e.getCheckInDate())
                .checkOutDate(e.getCheckOutDate())
                .guestCount(e.getGuestCount())
                .basePayment(e.getBasePayment())
                .reservationsStatus(e.getReservationsStatus())
                .reservationsDate(e.getReservationsDate())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    /** 엔티티 변환 (연관관계는 서비스에서 setMember/setRoom으로 보완 권장) */
    public ReservationEntity toEntity() {
        ReservationEntity e = new ReservationEntity();
        e.setId(this.id);
        e.setMemberId(this.memberId);
        e.setRoomId(this.roomId);
        e.setCheckInDate(this.checkInDate);
        e.setCheckOutDate(this.checkOutDate);
        e.setGuestCount(this.guestCount);
        e.setBasePayment(this.basePayment);
        e.setReservationsStatus(this.reservationsStatus);
        e.setReservationsDate(this.reservationsDate);
        e.setUpdatedAt(this.updatedAt);
        return e;
    }

    /* ================== IReservation 기본 구현 위임 ================== */

    @Override public void copyReservation(IReservation src) { IReservation.super.copyReservation(src); }
    @Override public void copyNotNullReservation(IReservation src) { IReservation.super.copyNotNullReservation(src); }

    /* ================== 조회 응답용 내부 클래스 ================== */

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RoomInfo {
        private Long id;
        private String roomName;
        private String roomView;
        private BigDecimal basePrice;
    }
}

