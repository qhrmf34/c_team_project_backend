package com.hotel_project.payment_jpa.reservations.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hotel_project.common_jpa.dto.IId;
import com.hotel_project.hotel_jpa.room.dto.RoomDto;
import com.hotel_project.member_jpa.member.dto.MemberDto;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationsDto implements IReservations {
    private Long id;

    @JsonIgnore
    private MemberDto memberDto;

    private Long memberId;

    @JsonIgnore
    private RoomDto roomDto;

    @NotNull(message = "객실 ID는 필수입니다.")
    @JsonProperty("roomId")
    private Long roomId;

    @NotNull(message = "체크인 날짜는 필수입니다.")
    private LocalDate checkInDate;

    @NotNull(message = "체크아웃 날짜는 필수입니다.")
    private LocalDate checkOutDate;

    @NotNull(message = "투숙객 수는 필수입니다.")
    private Integer guestsCount;

    @NotNull(message = "가격은 필수입니다.")
    @Digits(integer = 10, fraction = 2, message = "가격은 최대 10자리 정수, 2자리 소수까지 가능합니다.")
    private BigDecimal basePayment;

    @NotNull(message = "예약 상태는 필수입니다.")
    private Boolean reservationsStatus;

    private LocalDateTime reservationsAt;
    private LocalDateTime updatedAt;

    // ===== Member 관련 =====

    @Override
    public IId getMember() {
        return memberDto;
    }

    @Override
    public void setMember(IId iId) {
        if (iId == null) {
            return;
        }
        if (this.memberDto == null) {
            this.memberDto = new MemberDto();
        }
        this.memberDto.copyMembersId(iId);
    }

    // Lombok이 생성한 getMemberId()를 사용하도록 @Override 제거
    // 또는 명시적으로 구현
    public Long getMemberId() {
        if (this.memberDto != null && this.memberDto.getId() != null) {
            return this.memberDto.getId();
        }
        return this.memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
        if (memberId != null) {
            if (this.memberDto == null) {
                this.memberDto = new MemberDto();
            }
            this.memberDto.setId(memberId);
        }
    }

    // ===== Room 관련 =====

    @Override
    public IId getRoom() {
        return roomDto;
    }

    @Override
    public void setRoom(IId iId) {
        if (iId == null) {
            return;
        }
        if (this.roomDto == null) {
            this.roomDto = new RoomDto();
        }
        this.roomDto.copyMembersId(iId);  // ✅ roomDto에 복사
    }

    // @Override 제거하고 커스텀 구현
    public Long getRoomId() {
        if (this.roomDto != null && this.roomDto.getId() != null) {
            return this.roomDto.getId();
        }
        return this.roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;  // ✅ 필드에 직접 설정
        if (roomId != null) {
            if (this.roomDto == null) {
                this.roomDto = new RoomDto();
            }
            this.roomDto.setId(roomId);
        }
    }
}