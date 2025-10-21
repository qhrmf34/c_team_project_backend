package com.hotel_project.payment_jpa.reservations.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.payment_jpa.reservations.dto.ReservationsDto;
import com.hotel_project.payment_jpa.reservations.dto.ReservationsEntity;
import com.hotel_project.payment_jpa.reservations.dto.ReservationSummaryDto;
import com.hotel_project.common_jpa.exception.DuplicateReservationException;
import com.hotel_project.payment_jpa.reservations.repository.ReservationsRepository;
import com.hotel_project.payment_jpa.reservations.mapper.ReservationsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationsService {

    private final ReservationsRepository reservationsRepository;
    private final ReservationsMapper reservationsMapper;

    /**
     * 예약 생성
     */
    public ReservationsDto createReservation(ReservationsDto reservationsDto, Long memberId) throws CommonExceptionTemplate {
        try {
            if (reservationsDto == null) {
                throw MemberException.INVALID_DATA.getException();
            }

            if (memberId == null) {
                throw MemberException.INVALID_ID.getException();
            }

            validateReservationDates(reservationsDto.getCheckInDate(), reservationsDto.getCheckOutDate());

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

    /**
     * 날짜 유효성 검증
     */
    private void validateReservationDates(LocalDate checkInDate, LocalDate checkOutDate) throws CommonExceptionTemplate {
        LocalDate today = LocalDate.now();

        if (checkInDate.isBefore(today)) {
            log.warn("과거 날짜 예약 시도 - 체크인: {}, 오늘: {}", checkInDate, today);
            throw new CommonExceptionTemplate(400, "체크인 날짜는 오늘 이후여야 합니다.");
        }

        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            log.warn("잘못된 날짜 예약 시도 - 체크인: {}, 체크아웃: {}", checkInDate, checkOutDate);
            throw new CommonExceptionTemplate(400, "체크아웃 날짜는 체크인 날짜보다 이후여야 합니다.");
        }

        if (checkOutDate.isBefore(today)) {
            log.warn("과거 날짜 예약 시도 - 체크아웃: {}, 오늘: {}", checkOutDate, today);
            throw new CommonExceptionTemplate(400, "체크아웃 날짜가 이미 지났습니다.");
        }
    }

    /**
     * 예약 상태 업데이트
     */
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

    /**
     * 미결제 예약 삭제
     */
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

    /**
     * 내 예약 목록 조회 (페이지네이션 지원)
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getMyReservations(Long memberId, Integer offset, Integer size) throws CommonExceptionTemplate {
        try {
            log.info("예약 목록 조회 시작 - 회원 ID: {}, offset: {}, size: {}", memberId, offset, size);

            // 전체 개수 조회
            int totalCount = reservationsMapper.countReservationsByMemberId(memberId);

            // 예약 목록 조회
            List<ReservationSummaryDto> reservations = reservationsMapper.findReservationsByMemberId(
                    memberId, offset, size);

            log.info("예약 목록 조회 완료 - 회원 ID: {}, 예약 개수: {}, 전체: {}",
                    memberId, reservations.size(), totalCount);

            Map<String, Object> result = new HashMap<>();
            result.put("reservations", reservations);
            result.put("totalCount", totalCount);

            return result;

        } catch (Exception e) {
            log.error("예약 목록 조회 중 오류 발생 - 회원 ID: {}", memberId, e);
            throw new CommonExceptionTemplate(500, "예약 목록 조회 중 오류가 발생했습니다");
        }
    }

    // ✅ 전체 조회 메서드도 유지 (기존 호환성)
    @Transactional(readOnly = true)
    public List<ReservationSummaryDto> getMyReservations(Long memberId) throws CommonExceptionTemplate {
        Map<String, Object> result = getMyReservations(memberId, null, null);
        return (List<ReservationSummaryDto>) result.get("reservations");
    }
}