package com.hotel_project.member_jpa.reservations.dto;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.hotel_project.member_jpa.member.dto.MemberEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reservations_tbl")
public class ReservationEntity implements IReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // PK 자동 증가

    /** member_id FK 원시값 (읽기 전용) */
    @Column(name = "member_id", nullable = false, insertable = false, updatable = false)
    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_reservation_member")
    )
    private MemberEntity member;


    @Column(name = "room_id", nullable = false, insertable = false, updatable = false)
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "room_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_reservation_room")
    )
    private RoomEntity room; //룸엔티티 구현이 되지않아 오류발생

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDate checkOutDate;

    @Column(name = "guest_count", nullable = false)
    private Integer guestCount;

    @Column(name = "base_payment", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePayment;

    @Column(name = "reservations_status", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean reservationsStatus;

    @Column(name = "reservations_date", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime reservationsDate;

    @Column(name = "updated_at",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (reservationsDate == null) reservationsDate = now;
        if (updatedAt == null) updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }


    @Override
    public Long getMemberId() {
        if (memberId != null) return memberId;
        return (member != null ? member.getId() : null);
    }

    @Override
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    @Override
    public Long getRoomId() {
        if (roomId != null) return roomId;
        return (room != null ? room.getId() : null);
    }

    @Override
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}
