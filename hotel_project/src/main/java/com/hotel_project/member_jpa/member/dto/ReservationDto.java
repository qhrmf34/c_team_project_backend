package com.hotel_project.member_jpa.member.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {

    private Long id;
    private Long memberId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer guestCount;
    private BigDecimal basePayment;
    private Integer reservationsStatus;
    private LocalDateTime reservationsDate;
    private LocalDateTime updatedAt;
}
