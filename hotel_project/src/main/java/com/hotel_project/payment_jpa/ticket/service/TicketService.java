package com.hotel_project.payment_jpa.ticket.service;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.hotel_jpa.hotel.mapper.HotelMapper;
import com.hotel_project.hotel_jpa.room.mapper.RoomMapper;
import com.hotel_project.member_jpa.member.mapper.MemberMapper;
import com.hotel_project.member_jpa.member.dto.MemberDto;
import com.hotel_project.member_jpa.member_image.service.MemberImageService;
import com.hotel_project.member_jpa.member_image.dto.ImageType;
import com.hotel_project.payment_jpa.payments.repository.PaymentsRepository;
import com.hotel_project.payment_jpa.payments.dto.PaymentsEntity;
import com.hotel_project.payment_jpa.reservations.repository.ReservationsRepository;
import com.hotel_project.payment_jpa.reservations.dto.ReservationsEntity;
import com.hotel_project.payment_jpa.ticket.repository.TicketRepository;
import com.hotel_project.payment_jpa.ticket.dto.TicketEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;
    private final PaymentsRepository paymentsRepository;
    private final ReservationsRepository reservationsRepository;
    private final MemberMapper memberMapper;
    private final RoomMapper roomMapper;
    private final HotelMapper hotelMapper;
    private final MemberImageService memberImageService;

    public Map<String, Object> getTicketDetailByPaymentId(Long paymentId, Long memberId)
            throws CommonExceptionTemplate {

        try {
            log.info("티켓 상세 조회 - paymentId: {}, memberId: {}", paymentId, memberId);

            // 1. 결제 정보 조회
            PaymentsEntity payment = paymentsRepository.findById(paymentId)
                    .orElseThrow(() -> new CommonExceptionTemplate(404, "결제 정보를 찾을 수 없습니다"));

            // 2. 티켓 조회
            TicketEntity ticket = ticketRepository.findByPaymentsEntity_Id(paymentId)
                    .orElseThrow(() -> new CommonExceptionTemplate(404, "티켓을 찾을 수 없습니다"));

            // 3. ✅ Repository로 예약 정보 조회
            ReservationsEntity reservation = reservationsRepository.findById(payment.getReservationsId())
                    .orElseThrow(() -> new CommonExceptionTemplate(404, "예약 정보를 찾을 수 없습니다"));

            // 4. 권한 확인
            if (!reservation.getMemberId().equals(memberId)) {
                throw new CommonExceptionTemplate(403, "접근 권한이 없습니다");
            }

            // 5. 회원 정보 조회
            MemberDto member = memberMapper.findById(memberId);
            if (member == null) {
                throw new CommonExceptionTemplate(404, "회원 정보를 찾을 수 없습니다");
            }

            // 6. 회원 프로필 이미지 조회
            String profileImage = memberImageService.getMemberImage(memberId, ImageType.profile);

            // 7. 객실 정보 조회
            Map<String, Object> room = roomMapper.getRoomDetailById(reservation.getRoomId());
            if (room == null) {
                throw new CommonExceptionTemplate(404, "객실 정보를 찾을 수 없습니다");
            }

            // 8. 호텔 정보 조회
            Map<String, Object> hotel = hotelMapper.getHotelBasicInfo((Long) room.get("hotelId"));
            if (hotel == null) {
                throw new CommonExceptionTemplate(404, "호텔 정보를 찾을 수 없습니다");
            }

            // 9. 호텔 이미지 조회
            String hotelImage = hotelMapper.getFirstHotelImage((Long) room.get("hotelId"));

            // 10. 침대 정보 구성
            String bedInfo = buildBedInfo(
                    ((Number) room.get("roomSingleBed")).intValue(),
                    ((Number) room.get("roomDoubleBed")).intValue()
            );

            // 11. 회원 이름 구성
            String memberName = buildMemberName(member);

            // 12. 응답 데이터 구성
            Map<String, Object> result = new HashMap<>();

            result.put("ticketId", ticket.getId());
            result.put("barcode", ticket.getBarcode());
            result.put("isUsed", ticket.getIsUsed());
            result.put("createdAt", ticket.getCreatedAt());

            result.put("memberId", memberId);
            result.put("memberName", memberName);
            result.put("profileImage", profileImage);

            result.put("hotelName", hotel.get("hotelName"));
            result.put("hotelImage", hotelImage);
            result.put("countryName", hotel.get("countryName"));
            result.put("cityName", hotel.get("cityName"));
            result.put("address", hotel.get("address"));
            result.put("checkInTime",hotel.get("checkinTime"));
            result.put("checkOutTime",hotel.get("checkoutTime"));
            result.put("roomName", room.get("roomName"));
            result.put("roomNumber", room.get("roomNumber"));
            result.put("bedInfo", bedInfo);
            result.put("ticketImagePath", ticket.getTicketImageName());

            result.put("checkInDate", reservation.getCheckInDate());
            result.put("checkOutDate", reservation.getCheckOutDate());
            result.put("reservationId", reservation.getId());

            result.put("paymentId", payment.getId());
            result.put("paymentAmount", payment.getPaymentAmount());
            result.put("paymentDate", payment.getPaymentDate());
            result.put("refund", payment.getRefund());

            log.info("✅ 티켓 상세 조회 완료");
            return result;

        } catch (CommonExceptionTemplate e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ 티켓 상세 조회 실패", e);
            throw new CommonExceptionTemplate(500, "티켓 조회 중 오류 발생: " + e.getMessage());
        }
    }

    private String buildBedInfo(int singleBed, int doubleBed) {
        StringBuilder bedInfo = new StringBuilder();

        if (singleBed > 0) {
            bedInfo.append(singleBed).append(" Single Bed");
        }

        if (singleBed > 0 && doubleBed > 0) {
            bedInfo.append(" or ");
        }

        if (doubleBed > 0) {
            bedInfo.append(doubleBed).append(" Double Bed");
        }

        if (bedInfo.length() == 0) {
            bedInfo.append("Bed information not available");
        }

        return bedInfo.toString();
    }

    private String buildMemberName(MemberDto member) {
        String provider = member.getProvider() != null ? member.getProvider().name() : "local";

        if ("kakao".equals(provider) || "google".equals(provider) || "naver".equals(provider)) {
            if (member.getFirstName() != null && !member.getFirstName().trim().isEmpty()) {
                return member.getFirstName();
            } else if (member.getEmail() != null) {
                return member.getEmail().split("@")[0];
            }
            return "Guest";
        }

        if ("local".equals(provider)) {
            if (member.getFirstName() != null && member.getLastName() != null) {
                return member.getFirstName() + " " + member.getLastName();
            } else if (member.getFirstName() != null) {
                return member.getFirstName();
            } else if (member.getEmail() != null) {
                return member.getEmail().split("@")[0];
            }
        }

        return "Guest";
    }

    @Transactional
    public void updateTicketImage(Long ticketId, String imagePath, Long memberId)
            throws CommonExceptionTemplate {
        try {
            TicketEntity ticket = ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new CommonExceptionTemplate(404, "티켓을 찾을 수 없습니다"));

            // 권한 확인
            PaymentsEntity payment = paymentsRepository.findById(ticket.getPaymentId())
                    .orElseThrow(() -> new CommonExceptionTemplate(404, "결제 정보를 찾을 수 없습니다"));

            ReservationsEntity reservation = reservationsRepository.findById(payment.getReservationsId())
                    .orElseThrow(() -> new CommonExceptionTemplate(404, "예약 정보를 찾을 수 없습니다"));

            if (!reservation.getMemberId().equals(memberId)) {
                throw new CommonExceptionTemplate(403, "접근 권한이 없습니다");
            }

            // 이미지 경로 업데이트
            ticket.setTicketImageName(imagePath);
            ticketRepository.save(ticket);

            log.info("✅ 티켓 이미지 경로 업데이트 완료 - ticketId: {}", ticketId);

        } catch (Exception e) {
            log.error("❌ 티켓 이미지 업데이트 실패", e);
            throw new CommonExceptionTemplate(500, "티켓 이미지 업데이트 실패");
        }
    }
}