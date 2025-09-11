package com.hotel_project.member_jpa.member.dto;

import com.hotel_project.member_jpa.member.dto.MemberEntity;
import com.hotel_project.member_jpa.member.dto.ReservationDto;
import com.hotel_project.member_jpa.member.dto.ReservationEntity;
import com.hotel_project.member_jpa.member.dto.RoomEntity;
import com.hotel_project.member_jpa.member.repository.MemberRepository;
import com.hotel_project.member_jpa.member.repository.ReservationRepository;
import com.hotel_project.member_jpa.member.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;

    public ReservationDto createReservation(ReservationDto reservationDto) {

        MemberEntity member = memberRepository.findById(reservationDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        RoomEntity room = roomRepository.findById(reservationDto.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid room ID"));

        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setMember(member);
        reservationEntity.setRoom(room);
        reservationEntity.setCheckInDate(reservationDto.getCheckInDate());
        reservationEntity.setCheckOutDate(reservationDto.getCheckOutDate());
        reservationEntity.setGuestCount(reservationDto.getGuestCount());
        reservationEntity.setBasePayment(reservationDto.getBasePayment());
        reservationEntity.setReservationsStatus(reservationDto.getReservationsStatus());
        ReservationEntity savedReservation = reservationRepository.save(reservationEntity);
        return convertToDto(savedReservation);
    }

    public List<ReservationDto> getReservationsByMemberId(Long memberId) {
        List<ReservationEntity> reservations = reservationRepository.findByMemberId(memberId);
        return reservations.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<ReservationDto> getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .map(this::convertToDto);
    }
    public ReservationDto updateReservationStatus(Long reservationId, Integer status) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        reservation.setReservationsStatus(status);
        ReservationEntity updatedReservation = reservationRepository.save(reservation);
        return convertToDto(updatedReservation);
    }
    private ReservationDto convertToDto(ReservationEntity entity) {
        return ReservationDto.builder()
                .id(entity.getId())
                .memberId(entity.getMember().getId())
                .roomId(entity.getRoom().getId())
                .checkInDate(entity.getCheckInDate())
                .checkOutDate(entity.getCheckOutDate())
                .guestCount(entity.getGuestCount())
                .basePayment(entity.getBasePayment())
                .reservationsStatus(entity.getReservationsStatus())
                .reservationsDate(entity.getReservationsDate())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
