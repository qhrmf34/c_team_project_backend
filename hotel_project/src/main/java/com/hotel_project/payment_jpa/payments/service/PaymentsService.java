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
            // 1. 결제수단 조회
            PaymentMethodDto paymentMethod = paymentMethodMapper.findById(paymentsDto.getPaymentMethodId());
            if (paymentMethod == null) {
                throw MemberException.NOT_EXIST_DATA.getException();
            }

            // 2. 본인의 결제수단인지 확인
            if (!paymentMethod.getMemberId().equals(memberId)) {
                throw new CommonExceptionTemplate(403, "해당 결제수단에 대한 권한이 없습니다");
            }

            // 3. 주문 ID 생성
            String orderId = "ORDER_" + System.currentTimeMillis() + "_" + paymentsDto.getReservationsId();
            String orderName = "호텔 예약 결제";

            // 4. 토스 결제 API 호출
            TossPaymentResponseDto tossResponse = tossPaymentService.processPayment(
                    paymentMethod.getTossKey(),  // 빌링키
                    paymentsDto.getPaymentAmount(),  // 결제 금액
                    orderId,  // 주문 ID
                    orderName,  // 주문명
                    memberId  // 회원 ID
            );

            // 5. 결제 정보 저장
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

            // 6. 예약 상태를 true(확정)로 업데이트
            reservationsService.updateReservationStatus(paymentsDto.getReservationsId(), true);

            // 7. 티켓 생성
            TicketEntity ticket = new TicketEntity();
            ticket.setPaymentId(savedEntity.getId());
            ticket.setTicketImageName("TICKET_" + UUID.randomUUID().toString());
            ticket.setIsUsed(false);
            ticket.setCreatedAt(LocalDateTime.now());

            ticketRepository.save(ticket);

            // 8. DTO로 변환
            PaymentsDto resultDto = new PaymentsDto();
            resultDto.copyMembers(savedEntity);

            log.info("결제 완료 - 결제 ID: {}, 예약 ID: {}, 금액: {}, 토스 결제키: {}",
                    savedEntity.getId(), savedEntity.getReservationsId(), savedEntity.getPaymentAmount(), tossResponse.getPaymentKey());

            return resultDto;

        } catch (CommonExceptionTemplate e) {
            throw e;
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생", e);
            throw new CommonExceptionTemplate(500, "결제 처리 중 오류가 발생했습니다");
        }
    }
}