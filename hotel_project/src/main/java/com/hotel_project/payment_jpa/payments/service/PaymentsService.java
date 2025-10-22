package com.hotel_project.payment_jpa.payments.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.payment_jpa.coupon.dto.CouponEntity;
import com.hotel_project.payment_jpa.coupon.repository.CouponRepository;
import com.hotel_project.payment_jpa.member_coupon.dto.MemberCouponEntity;
import com.hotel_project.payment_jpa.member_coupon.repository.MemberCouponRepository;
import com.hotel_project.payment_jpa.payments.dto.PaymentsDto;
import com.hotel_project.payment_jpa.payments.dto.PaymentsEntity;
import com.hotel_project.payment_jpa.payments.dto.PaymentStatus;
import com.hotel_project.payment_jpa.payments.repository.PaymentsRepository;
import com.hotel_project.payment_jpa.reservations.dto.ReservationsEntity;
import com.hotel_project.payment_jpa.reservations.repository.ReservationsRepository;
import com.hotel_project.payment_jpa.payment_method.mapper.PaymentMethodMapper;
import com.hotel_project.payment_jpa.payment_method.dto.PaymentMethodDto;
import com.hotel_project.payment_jpa.payment_method.service.TossPaymentService;
import com.hotel_project.payment_jpa.payment_method.dto.TossPaymentResponseDto;
import com.hotel_project.payment_jpa.reservations.service.ReservationsService;
import com.hotel_project.payment_jpa.ticket.dto.TicketEntity;
import com.hotel_project.payment_jpa.ticket.repository.TicketRepository;
import com.hotel_project.payment_jpa.ticket.service.TicketImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private final PaymentMethodMapper paymentMethodMapper;
    private final TicketRepository ticketRepository;
    private final TossPaymentService tossPaymentService;
    private final ReservationsService reservationsService;
    private final ReservationsRepository reservationsRepository;
    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final TicketImageService ticketImageService;
    public PaymentsDto processPayment(PaymentsDto paymentsDto, Long memberId) throws CommonExceptionTemplate {
        try {
            log.info("=== 결제 처리 시작 ===");
            log.info("예약 ID: {}", paymentsDto.getReservationsId());
            log.info("결제수단 ID: {}", paymentsDto.getPaymentMethodId());
            log.info("금액: {}", paymentsDto.getPaymentAmount());

            // 1. 결제수단 조회
            PaymentMethodDto paymentMethod = paymentMethodMapper.findById(paymentsDto.getPaymentMethodId());
            if (paymentMethod == null) {
                throw MemberException.NOT_EXIST_DATA.getException();
            }

            // 2. 본인의 결제수단인지 확인
            if (!paymentMethod.getMemberId().equals(memberId)) {
                throw new CommonExceptionTemplate(403, "해당 결제수단에 대한 권한이 없습니다");
            }

            // 3. 주문 정보 생성
            String orderId = "ORDER_" + System.currentTimeMillis() + "_" + paymentsDto.getReservationsId();
            String orderName = "호텔 예약 결제";

            log.info("빌링키: {}", paymentMethod.getTossKey());
            log.info("주문 ID: {}", orderId);

            // 4. ✅✅ 새로운 메서드로 결제 처리 (widgetSecretKey 사용)
            TossPaymentResponseDto tossResponse = tossPaymentService.processPaymentWithBillingKey(
                    paymentMethod.getTossKey(),  // 저장된 빌링키
                    paymentsDto.getPaymentAmount(),
                    orderId,
                    orderName,
                    memberId
            );

            log.info("✅ 토스 결제 성공 - paymentKey: {}", tossResponse.getPaymentKey());

            // 5. 결제 정보 DB 저장
            PaymentsEntity entity = new PaymentsEntity();
            entity.setReservationsId(paymentsDto.getReservationsId());
            entity.setPaymentMethodId(paymentsDto.getPaymentMethodId());
            entity.setCouponId(paymentsDto.getCouponId());
            entity.setPaymentAmount(paymentsDto.getPaymentAmount());
            entity.setPaymentDate(LocalDateTime.now());
            entity.setPaymentStatus(PaymentStatus.paid);
            entity.setTossPaymentKey(tossResponse.getPaymentKey());
            entity.setRefund(false);

            PaymentsEntity savedEntity = paymentsRepository.save(entity);

            // 6. 예약 상태 확정
            reservationsService.updateReservationStatus(paymentsDto.getReservationsId(), true);

            // 7. 티켓 생성
            TicketEntity ticket = new TicketEntity();
            ticket.setPaymentId(savedEntity.getId());
            ticket.setTicketImageName("TICKET_" + UUID.randomUUID().toString());
            ticket.setIsUsed(false);
            ticket.setCreatedAt(LocalDateTime.now());

            ticketRepository.save(ticket);

            // 8. DTO 변환
            PaymentsDto resultDto = new PaymentsDto();
            resultDto.copyMembers(savedEntity);

            log.info("✅✅ 결제 완료! - 결제 ID: {}, 예약 ID: {}, 토스 결제키: {}",
                    savedEntity.getId(), savedEntity.getReservationsId(), tossResponse.getPaymentKey());

            return resultDto;

        } catch (CommonExceptionTemplate e) {
            log.error("❌ 결제 처리 실패 - CommonException: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ 결제 처리 중 예상치 못한 오류", e);
            throw new CommonExceptionTemplate(500, "결제 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    /**
     * ✅ 신규: 결제위젯 승인 및 저장
     */
    public PaymentsDto confirmWidgetPayment(
            String paymentKey,
            String orderId,
            Long amount,
            Long reservationId,
            Long paymentMethodId,
            Long couponId,
            Long memberId) throws CommonExceptionTemplate {

        try {
            log.info("=== 결제위젯 승인 처리 시작 ===");
            log.info("paymentKey: {}, orderId: {}, amount: {}", paymentKey, orderId, amount);
            log.info("reservationId: {}, couponId: {}", reservationId, couponId);

            // 1. 예약 정보 조회
            ReservationsEntity reservation = reservationsRepository.findById(reservationId)
                    .orElseThrow(() -> new CommonExceptionTemplate(404, "예약 정보를 찾을 수 없습니다"));

            // 2. 날짜 검증 추가
            validatePaymentDates(reservation.getCheckInDate(), reservation.getCheckOutDate());

            // 3. 이미 결제된 예약인지 확인
            if (reservation.getReservationsStatus()) {
                throw new CommonExceptionTemplate(400, "이미 결제가 완료된 예약입니다.");
            }

            // 4. 백엔드에서 최종 금액 계산
            Long calculatedAmount = calculateFinalAmount(reservation, couponId);

            // 5. 프론트에서 보낸 금액과 백엔드 계산 금액 일치 확인
            if (!calculatedAmount.equals(amount)) {
                log.error("금액 불일치 - 프론트: {}, 백엔드 계산: {}", amount, calculatedAmount);
                throw new CommonExceptionTemplate(400, "결제 금액이 일치하지 않습니다.");
            }

            // 6. 토스에 결제 승인 요청
            TossPaymentResponseDto tossResponse =
                    tossPaymentService.confirmWidgetPayment(paymentKey, orderId, calculatedAmount);

            log.info("✅ 토스 승인 완료 - paymentKey: {}", tossResponse.getPaymentKey());

            // 7. 결제 정보 DB 저장
            PaymentsEntity entity = new PaymentsEntity();
            entity.setReservationsId(reservationId);

            if (couponId != null && couponId > 0) {
                entity.setCouponId(couponId);
            }

            entity.setPaymentAmount(calculatedAmount);
            entity.setPaymentDate(LocalDateTime.now());
            entity.setPaymentStatus(PaymentStatus.paid);
            entity.setTossPaymentKey(tossResponse.getPaymentKey());
            entity.setRefund(false);

            PaymentsEntity savedEntity = paymentsRepository.save(entity);

            // 8. 쿠폰 사용 처리
            if (couponId != null && couponId > 0) {
                MemberCouponEntity memberCoupon = memberCouponRepository
                        .findByMemberEntity_IdAndCouponEntity_IdAndIsUsedFalse(memberId, couponId)
                        .orElseThrow(() -> new CommonExceptionTemplate(404, "사용 가능한 쿠폰을 찾을 수 없습니다"));

                memberCoupon.setIsUsed(true);
                memberCoupon.setUsedAt(LocalDateTime.now());
                memberCouponRepository.save(memberCoupon);

                log.info("✅ 쿠폰 사용 처리 완료 - couponId: {}", couponId);
            }

            // 9. 예약 상태 확정
            reservationsService.updateReservationStatus(reservationId, true);

            // 10. ✅ 티켓 생성 (이미지는 프론트에서 업로드)
            TicketEntity ticket = new TicketEntity();
            ticket.setPaymentId(savedEntity.getId());
            ticket.setTicketImageName(null); // 나중에 프론트에서 업로드
            ticket.setIsUsed(false);
            ticket.setCreatedAt(LocalDateTime.now());

            ticketRepository.save(ticket);
            log.info("✅ 티켓 생성 완료 - ticketId: {}", ticket.getId());

            // 11. DTO 변환
            PaymentsDto resultDto = new PaymentsDto();
            resultDto.copyMembers(savedEntity);

            log.info("✅✅ 결제 완료! - 결제 ID: {}", savedEntity.getId());

            return resultDto;

        } catch (CommonExceptionTemplate e) {
            log.error("❌ 결제 승인 처리 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ 결제 승인 처리 중 예상치 못한 오류", e);
            throw new CommonExceptionTemplate(500, "결제 승인 중 오류: " + e.getMessage());
        }
    }


    // ✅ 결제 시 날짜 검증 메서드 추가
    private void validatePaymentDates(LocalDate checkInDate, LocalDate checkOutDate) throws CommonExceptionTemplate {
        LocalDate today = LocalDate.now();

        // 1. 체크인 날짜가 과거인지 확인
        if (checkInDate.isBefore(today)) {
            log.warn("과거 날짜 결제 시도 - 체크인: {}, 오늘: {}", checkInDate, today);
            throw new CommonExceptionTemplate(400, "체크인 날짜가 이미 지났습니다. 결제를 진행할 수 없습니다.");
        }

        // 2. 체크아웃이 체크인보다 이전이거나 같은지 확인
        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            log.warn("잘못된 날짜 결제 시도 - 체크인: {}, 체크아웃: {}", checkInDate, checkOutDate);
            throw new CommonExceptionTemplate(400, "잘못된 날짜입니다. 결제를 진행할 수 없습니다.");
        }

        // 3. 체크아웃이 과거인지 확인
        if (checkOutDate.isBefore(today)) {
            log.warn("과거 날짜 결제 시도 - 체크아웃: {}, 오늘: {}", checkOutDate, today);
            throw new CommonExceptionTemplate(400, "체크아웃 날짜가 이미 지났습니다. 결제를 진행할 수 없습니다.");
        }
    }
    /**
     * ✅ 전액 환불 (티켓 상태 업데이트 포함)
     */
// PaymentsService.java의 refundPayment 메서드 수정
    public PaymentsDto refundPayment(Long paymentId, String cancelReason, Long memberId)
            throws CommonExceptionTemplate {

        try {
            log.info("=== 환불 처리 시작 - paymentId: {} ===", paymentId);

            PaymentsEntity payment = paymentsRepository.findById(paymentId)
                    .orElseThrow(() -> new CommonExceptionTemplate(404, "결제 정보를 찾을 수 없습니다"));

            if (payment.getRefund()) {
                throw new CommonExceptionTemplate(400, "이미 환불된 결제입니다");
            }

            if (payment.getPaymentStatus() != PaymentStatus.paid) {
                throw new CommonExceptionTemplate(400, "환불 가능한 상태가 아닙니다");
            }

            // 토스에 환불 요청
            TossPaymentResponseDto tossResponse =
                    tossPaymentService.cancelPayment(payment.getTossPaymentKey(), cancelReason);

            log.info("✅ 토스 환불 완료");

            // DB 업데이트
            payment.setRefund(true);
            payment.setRefundDate(LocalDateTime.now());
            payment.setPaymentStatus(PaymentStatus.refunded);
            payment.setUpdatedAt(LocalDateTime.now());

            PaymentsEntity updatedPayment = paymentsRepository.save(payment);

            // 예약 상태 업데이트
            reservationsService.updateReservationStatus(payment.getReservationsId(), false);

            // ✅ 티켓 삭제
            ticketRepository.findByPaymentsEntity_Id(paymentId).ifPresent(ticket -> {
                // 티켓 이미지 파일 삭제
                if (ticket.getTicketImageName() != null) {
                    ticketImageService.deleteTicketImage(ticket.getTicketImageName());
                }

                // 티켓 DB 삭제
                ticketRepository.delete(ticket);
                log.info("✅ 티켓 삭제 완료 - ticketId: {}", ticket.getId());
            });

            PaymentsDto resultDto = new PaymentsDto();
            resultDto.copyMembers(updatedPayment);

            log.info("✅✅ 환불 완료! - 결제 ID: {}", paymentId);

            return resultDto;

        } catch (CommonExceptionTemplate e) {
            log.error("❌ 환불 처리 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ 환불 처리 중 예상치 못한 오류", e);
            throw new CommonExceptionTemplate(500, "환불 처리 중 오류: " + e.getMessage());
        }
    }

    /**
     * ✅ 부분 환불
     */
    public PaymentsDto refundPaymentPartial(
            Long paymentId,
            Long refundAmount,
            String cancelReason,
            Long memberId) throws CommonExceptionTemplate {

        try {
            log.info("=== 부분 환불 처리 시작 - paymentId: {}, amount: {} ===",
                    paymentId, refundAmount);

            PaymentsEntity payment = paymentsRepository.findById(paymentId)
                    .orElseThrow(() -> new CommonExceptionTemplate(404, "결제 정보를 찾을 수 없습니다"));

            if (payment.getRefund()) {
                throw new CommonExceptionTemplate(400, "이미 환불된 결제입니다");
            }

            if (refundAmount > payment.getPaymentAmount()) {
                throw new CommonExceptionTemplate(400, "환불 금액이 결제 금액을 초과합니다");
            }

            // 토스에 부분 환불 요청
            TossPaymentResponseDto tossResponse =
                    tossPaymentService.cancelPaymentPartial(
                            payment.getTossPaymentKey(),
                            refundAmount,
                            cancelReason);

            log.info("✅ 토스 부분 환불 완료");

            // 부분 환불은 refund를 true로 하지 않고, 금액만 조정
            // 또는 별도 테이블로 환불 내역 관리
            payment.setUpdatedAt(LocalDateTime.now());
            PaymentsEntity updatedPayment = paymentsRepository.save(payment);

            PaymentsDto resultDto = new PaymentsDto();
            resultDto.copyMembers(updatedPayment);

            log.info("✅✅ 부분 환불 완료!");

            return resultDto;

        } catch (Exception e) {
            log.error("❌ 부분 환불 처리 중 오류", e);
            throw new CommonExceptionTemplate(500, "부분 환불 중 오류 발생");
        }
    }
    // 4. 최종 결제 금액 계산 메서드 추가
    private Long calculateFinalAmount(ReservationsEntity reservation, Long couponId)
            throws CommonExceptionTemplate {
        // 1. 기본 결제 금액
        Long baseAmount = reservation.getBasePayment().longValue();

        // 2. 쿠폰이 없으면 기본 금액 반환
        if (couponId == null || couponId <= 0) {
            return baseAmount;
        }

        // 3. 쿠폰 조회 및 할인 적용
        try {
            CouponEntity coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new CommonExceptionTemplate(404, "쿠폰을 찾을 수 없습니다"));

            // 쿠폰 만료일 체크
            if (coupon.getLastDate().isBefore(LocalDate.now())) {
                throw new CommonExceptionTemplate(400, "만료된 쿠폰입니다.");
            }

            // 쿠폰 활성화 상태 체크
            if (!coupon.getIsActive()) {
                throw new CommonExceptionTemplate(400, "사용할 수 없는 쿠폰입니다.");
            }

            // ✅ BigDecimal을 double로 변환하여 할인 금액 계산
            double discountRate = coupon.getDiscount().doubleValue() / 100.0;
            Long discountAmount = (long) Math.floor(baseAmount * discountRate);
            Long finalAmount = baseAmount - discountAmount;

            log.info("쿠폰 적용 - 기본금액: {}, 할인율: {}%, 할인금액: {}, 최종금액: {}",
                    baseAmount, coupon.getDiscount(), discountAmount, finalAmount);

            return finalAmount;

        } catch (CommonExceptionTemplate e) {
            throw e;
        } catch (Exception e) {
            log.error("쿠폰 적용 중 오류", e);
            throw new CommonExceptionTemplate(500, "쿠폰 적용 중 오류가 발생했습니다");
        }
    }
    /**
     * ✅ 내 결제 내역 조회
     */
    public List<PaymentsDto> getMyPayments(Long memberId) throws CommonExceptionTemplate {
        try {
            // TODO: 회원의 결제 내역 조회 로직
            // 예약 테이블과 조인해서 해당 회원의 결제만 가져오기
            return new ArrayList<>();
        } catch (Exception e) {
            throw new CommonExceptionTemplate(500, "결제 내역 조회 실패");
        }
    }
}