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
}