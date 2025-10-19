package com.hotel_project.payment_jpa.reservations.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.payment_jpa.reservations.dto.ReservationsDto;
import com.hotel_project.payment_jpa.reservations.dto.ReservationsEntity;
import com.hotel_project.common_jpa.exception.DuplicateReservationException;
import com.hotel_project.payment_jpa.reservations.repository.ReservationsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationsService {

    private final ReservationsRepository reservationsRepository;

    public ReservationsDto createReservation(ReservationsDto reservationsDto, Long memberId) throws CommonExceptionTemplate {
        try {
            if (reservationsDto == null) {
                throw MemberException.INVALID_DATA.getException();
            }

            if (memberId == null) {
                throw MemberException.INVALID_ID.getException();
            }

            // 중복 예약 체크 (미결제 예약만)
            Optional<ReservationsEntity> existingReservation = reservationsRepository
                    .findByMemberEntity_IdAndRoomEntity_IdAndCheckInDateAndCheckOutDateAndReservationsStatusFalse(
                            memberId,
                            reservationsDto.getRoomId(),
                            reservationsDto.getCheckInDate(),
                            reservationsDto.getCheckOutDate()
                    );

            if (existingReservation.isPresent()) {
                log.warn("중복 예약 시도 - 회원 ID: {}, 객실 ID: {}, 체크인: {}, 체크아웃: {}",
                        memberId, reservationsDto.getRoomId(),
                        reservationsDto.getCheckInDate(), reservationsDto.getCheckOutDate());

                ReservationsEntity existing = existingReservation.get();
                ReservationsDto existingDto = new ReservationsDto();
                existingDto.copyMembers(existing);

                throw new DuplicateReservationException(
                        "이미 해당 날짜에 미결제 예약이 존재합니다.",
                        existingDto
                );
            }

            ReservationsEntity entity = new ReservationsEntity();
            entity.setMemberId(memberId);
            entity.setRoomId(reservationsDto.getRoomId());
            entity.setCheckInDate(reservationsDto.getCheckInDate());
            entity.setCheckOutDate(reservationsDto.getCheckOutDate());
            entity.setGuestsCount(reservationsDto.getGuestsCount() != null ? reservationsDto.getGuestsCount() : 1);
            entity.setBasePayment(reservationsDto.getBasePayment());
            entity.setReservationsStatus(false);
            entity.setReservationsAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            ReservationsEntity savedEntity = reservationsRepository.save(entity);

            ReservationsDto resultDto = new ReservationsDto();
            resultDto.copyMembers(savedEntity);

            log.info("예약 생성 완료 - 예약 ID: {}, 회원 ID: {}, 객실 ID: {}, 상태: {}",
                    savedEntity.getId(), savedEntity.getMemberId(), savedEntity.getRoomId(), savedEntity.getReservationsStatus());

            return resultDto;

        } catch (DuplicateReservationException e) {
            throw e;
        } catch (CommonExceptionTemplate e) {
            throw e;
        } catch (Exception e) {
            log.error("예약 생성 중 오류 발생", e);
            throw new CommonExceptionTemplate(500, "예약 생성 중 오류가 발생했습니다");
        }
    }

    public ReservationsDto updateReservationStatus(Long reservationId, Boolean status) throws CommonExceptionTemplate {
        try {
            ReservationsEntity entity = reservationsRepository.findById(reservationId)
                    .orElseThrow(() -> MemberException.NOT_EXIST_DATA.getException());

            entity.setReservationsStatus(status);
            entity.setUpdatedAt(LocalDateTime.now());

            ReservationsEntity savedEntity = reservationsRepository.save(entity);

            ReservationsDto resultDto = new ReservationsDto();
            resultDto.copyMembers(savedEntity);

            log.info("예약 상태 업데이트 - 예약 ID: {}, 상태: {}", reservationId, status);

            return resultDto;

        } catch (CommonExceptionTemplate e) {
            throw e;
        } catch (Exception e) {
            log.error("예약 상태 업데이트 중 오류 발생", e);
            throw new CommonExceptionTemplate(500, "예약 상태 업데이트 중 오류가 발생했습니다");
        }
    }

    public void deleteUnpaidReservation(Long reservationId) throws CommonExceptionTemplate {
        try {
            ReservationsEntity entity = reservationsRepository.findById(reservationId)
                    .orElseThrow(() -> MemberException.NOT_EXIST_DATA.getException());

            if (entity.getReservationsStatus()) {
                throw new CommonExceptionTemplate(400, "이미 결제된 예약은 삭제할 수 없습니다.");
            }

            reservationsRepository.delete(entity);
            log.info("미결제 예약 삭제 완료 - 예약 ID: {}", reservationId);

        } catch (CommonExceptionTemplate e) {
            throw e;
        } catch (Exception e) {
            log.error("예약 삭제 중 오류 발생", e);
            throw new CommonExceptionTemplate(500, "예약 삭제 중 오류가 발생했습니다");
        }
    }
}