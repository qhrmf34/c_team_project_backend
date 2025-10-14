package com.hotel_project.payment_jpa.payments.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.payment_jpa.payments.dto.PaymentsDto;
import com.hotel_project.payment_jpa.payments.dto.PaymentsEntity;
import com.hotel_project.payment_jpa.payments.dto.PaymentStatus;
import com.hotel_project.payment_jpa.payments.repository.PaymentsRepository;
import com.hotel_project.payment_jpa.payment_method.repository.PaymentMethodRepository;
import com.hotel_project.payment_jpa.payment_method.mapper.PaymentMethodMapper;
import com.hotel_project.payment_jpa.payment_method.dto.PaymentMethodDto;
import com.hotel_project.payment_jpa.payment_method.service.TossPaymentService;
import com.hotel_project.payment_jpa.payment_method.dto.TossPaymentResponseDto;
import com.hotel_project.payment_jpa.reservations.service.ReservationsService;
import com.hotel_project.payment_jpa.ticket.dto.TicketEntity;
import com.hotel_project.payment_jpa.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;
    private final TicketRepository ticketRepository;
    private final TossPaymentService tossPaymentService;
    private final ReservationsService reservationsService;

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
            log.info("paymentMethodId: {}, couponId: {}", paymentMethodId, couponId);

            // ✅ 결제수단 권한 확인 제거 (paymentMethodId가 0이거나 null일 수 있음)
            // 토스가 직접 결제 처리하므로 우리 DB의 결제수단 ID는 불필요

            // 2. 토스에 결제 승인 요청
            TossPaymentResponseDto tossResponse =
                    tossPaymentService.confirmWidgetPayment(paymentKey, orderId, amount);

            log.info("✅ 토스 승인 완료 - paymentKey: {}", tossResponse.getPaymentKey());

            // 3. 결제 정보 DB 저장
            PaymentsEntity entity = new PaymentsEntity();
            entity.setReservationsId(reservationId);

            // ✅ paymentMethodId는 저장하지 않음 (토스가 직접 처리)
            // entity.setPaymentMethodId(paymentMethodId);

            if (couponId != null && couponId > 0) {
                entity.setCouponId(couponId);
            }

            entity.setPaymentAmount(amount);
            entity.setPaymentDate(LocalDateTime.now());
            entity.setPaymentStatus(PaymentStatus.paid);
            entity.setTossPaymentKey(tossResponse.getPaymentKey());
            entity.setRefund(false);

            PaymentsEntity savedEntity = paymentsRepository.save(entity);

            // 4. 예약 상태 확정
            reservationsService.updateReservationStatus(reservationId, true);

            // 5. 티켓 생성
            TicketEntity ticket = new TicketEntity();
            ticket.setPaymentId(savedEntity.getId());
            ticket.setTicketImageName("TICKET_" + UUID.randomUUID().toString());
            // ✅ 바코드는 @PrePersist에서 자동 생성됨
            ticket.setIsUsed(false);
            ticket.setCreatedAt(LocalDateTime.now());

            ticketRepository.save(ticket);

            // 6. DTO 변환
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
    /**
     * ✅ 전액 환불 (티켓 상태 업데이트 포함)
     */
    public PaymentsDto refundPayment(Long paymentId, String cancelReason, Long memberId)
            throws CommonExceptionTemplate {

        try {
            log.info("=== 환불 처리 시작 - paymentId: {} ===", paymentId);

            // 1. 결제 정보 조회
            PaymentsEntity payment = paymentsRepository.findById(paymentId)
                    .orElseThrow(() -> new CommonExceptionTemplate(404, "결제 정보를 찾을 수 없습니다"));

            // 2. 이미 환불된 건인지 확인
            if (payment.getRefund()) {
                throw new CommonExceptionTemplate(400, "이미 환불된 결제입니다");
            }

            // 3. 결제 상태 확인
            if (payment.getPaymentStatus() != PaymentStatus.paid) {
                throw new CommonExceptionTemplate(400, "환불 가능한 상태가 아닙니다");
            }

            // 4. 토스에 환불 요청
            TossPaymentResponseDto tossResponse =
                    tossPaymentService.cancelPayment(payment.getTossPaymentKey(), cancelReason);

            log.info("✅ 토스 환불 완료");

            // 5. DB 업데이트
            payment.setRefund(true);
            payment.setRefundDate(LocalDateTime.now());
            payment.setPaymentStatus(PaymentStatus.refunded);
            payment.setUpdatedAt(LocalDateTime.now());

            PaymentsEntity updatedPayment = paymentsRepository.save(payment);

            // 6. 예약 상태 업데이트 (환불되면 예약도 취소)
            reservationsService.updateReservationStatus(payment.getReservationsId(), false);

            // 7. ✅ 티켓 사용 불가 처리
            ticketRepository.findByPaymentsEntity_Id(paymentId).ifPresent(ticket -> {
                ticket.setIsUsed(true); // 환불된 티켓은 사용 불가
                ticketRepository.save(ticket);
                log.info("티켓 사용 불가 처리 완료 - ticketId: {}", ticket.getId());
            });

            // 8. DTO 변환
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