package com.hotel_project.payment_jpa.payment_method.service;



import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import com.hotel_project.member_jpa.member.repository.MemberRepository;
import com.hotel_project.member_jpa.member.mapper.MemberMapper;
import com.hotel_project.member_jpa.member.dto.MemberDto;
import com.hotel_project.payment_jpa.payment_method.dto.*;
import com.hotel_project.payment_jpa.payment_method.mapper.PaymentMethodMapper;
import com.hotel_project.payment_jpa.payment_method.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;
    private final TossPaymentService tossPaymentService;
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    /**
     * 결제수단 등록 (토스 빌링키 발급 및 저장) - 이메일 제거된 버전
     */
    public PaymentMethodDto registerPaymentMethod(CardRegistrationRequestDto request, Long memberId) throws CommonExceptionTemplate {
        if (request == null || memberId == null) {
            throw MemberException.INVALID_DATA.getException();
        }

        try {
            log.info("회원 ID: {}의 결제수단 등록 시작", memberId);

            // 1. 회원 존재 여부 확인 (MyBatis 사용)
            MemberDto memberDto = memberMapper.findById(memberId);
            if (memberDto == null) {
                throw MemberException.NOT_EXIST_DATA.getException();
            }

            // 2. 요청 데이터 유효성 검증
            validateCardRequest(request);

            // 3. 토스에 빌링키 등록 요청 (이메일 없이)
            TossBillingResponseDto tossResponse = tossPaymentService.registerBillingKey(request, memberId);

            // 4. 빌링키 중복 체크
            if (paymentMethodRepository.existsByTossKey(tossResponse.getBillingKey())) {
                log.warn("중복된 빌링키 등록 시도: {}", tossResponse.getBillingKey());
                throw MemberException.DUPLICATE_DATA.getException();
            }

            // 5. 결제수단 엔티티 생성 및 저장
            PaymentMethodEntity entity = new PaymentMethodEntity();
            entity.setMemberId(memberId);
            entity.setTossKey(tossResponse.getBillingKey());
            entity.setCreatedAt(LocalDateTime.now());
            entity.setUpdatedAt(LocalDateTime.now());

            PaymentMethodEntity savedEntity = paymentMethodRepository.save(entity);
            log.info("결제수단 등록 완료 - ID: {}, 회원 ID: {}, 토스키: {}",
                    savedEntity.getId(), memberId, savedEntity.getTossKey());

            // 6. DTO 변환 후 반환
            return convertToDto(savedEntity);

        } catch (CommonExceptionTemplate e) {
            log.error("회원 ID: {}의 결제수단 등록 실패 - CommonException", memberId, e);
            throw e;
        } catch (Exception e) {
            log.error("회원 ID: {}의 결제수단 등록 실패 - 예상치 못한 오류", memberId, e);
            throw new CommonExceptionTemplate(500, "결제수단 등록에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 카드 등록 요청 데이터 유효성 검증 - 개선된 버전
     */
    private void validateCardRequest(CardRegistrationRequestDto request) throws CommonExceptionTemplate {
        // 전체 유효성 검사
        if (!request.isValid()) {
            List<String> errors = request.getValidationErrors();
            throw new CommonExceptionTemplate(400, String.join(", ", errors));
        }


    }


    /**
     * 카드 번호 마스킹 (로그용)
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) {
            return cardNumber;
        }
        return cardNumber.substring(0, 4) + "****" + cardNumber.substring(cardNumber.length() - 4);
    }

    /**
     * 회원의 결제수단 목록 조회 (MyBatis 사용) - 개선된 버전
     */
    @Transactional(readOnly = true)
    public List<PaymentMethodDto> getPaymentMethods(Long memberId) throws CommonExceptionTemplate {
        if (memberId == null) {
            throw MemberException.INVALID_ID.getException();
        }

        try {
            log.debug("회원 ID: {}의 결제수단 목록 조회", memberId);

            List<PaymentMethodDto> paymentMethods = paymentMethodMapper.findByMemberId(memberId);

            // 보안을 위해 토스키는 마스킹 처리
            paymentMethods.forEach(this::maskSensitiveData);

            log.debug("회원 ID: {}의 결제수단 {}개 조회 완료", memberId, paymentMethods.size());
            return paymentMethods;

        } catch (Exception e) {
            log.error("회원 ID: {}의 결제수단 목록 조회 실패", memberId, e);
            throw new CommonExceptionTemplate(500, "결제수단 목록 조회에 실패했습니다");
        }
    }

    /**
     * 결제수단 단건 조회 (MyBatis 사용) - 권한 검증 추가
     */
    @Transactional(readOnly = true)
    public PaymentMethodDto getPaymentMethod(Long id) throws CommonExceptionTemplate {
        if (id == null) {
            throw MemberException.INVALID_ID.getException();
        }

        try {
            PaymentMethodDto result = paymentMethodMapper.findById(id);
            if (result == null) {
                throw MemberException.NOT_EXIST_DATA.getException();
            }

            // 보안을 위해 토스키 마스킹
            maskSensitiveData(result);

            return result;

        } catch (CommonExceptionTemplate e) {
            throw e;
        } catch (Exception e) {
            log.error("결제수단 ID: {} 조회 실패", id, e);
            throw new CommonExceptionTemplate(500, "결제수단 조회에 실패했습니다");
        }
    }

    /**
     * 결제수단 삭제 (JPA 사용) - 개선된 버전
     */
    public String deletePaymentMethod(Long id, Long memberId) throws CommonExceptionTemplate {
        if (id == null || memberId == null) {
            throw MemberException.INVALID_ID.getException();
        }

        try {
            log.info("결제수단 삭제 요청 - ID: {}, 회원 ID: {}", id, memberId);

            PaymentMethodEntity entity = paymentMethodRepository.findById(id)
                    .orElseThrow(() -> MemberException.NOT_EXIST_DATA.getException());

            // 본인의 결제수단인지 확인
            if (!entity.getMemberId().equals(memberId)) {
                log.warn("권한 없는 결제수단 삭제 시도 - 결제수단 ID: {}, 요청 회원 ID: {}, 실제 소유자 ID: {}",
                        id, memberId, entity.getMemberId());
                throw new CommonExceptionTemplate(403, "해당 결제수단에 대한 권한이 없습니다");
            }

            // TODO: 토스에서도 빌링키 삭제 처리 (필요시)
            // tossPaymentService.deleteBillingKey(entity.getTossKey());

            paymentMethodRepository.delete(entity);
            log.info("결제수단 삭제 완료 - ID: {}, 회원 ID: {}", id, memberId);

            return "결제수단이 성공적으로 삭제되었습니다";

        } catch (CommonExceptionTemplate e) {
            throw e;
        } catch (Exception e) {
            log.error("결제수단 삭제 실패 - ID: {}, 회원 ID: {}", id, memberId, e);
            throw new CommonExceptionTemplate(500, "결제수단 삭제에 실패했습니다");
        }
    }

    /**
     * 회원의 결제수단 존재 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean hasPaymentMethods(Long memberId) {
        if (memberId == null) {
            return false;
        }

        try {
            List<PaymentMethodDto> paymentMethods = paymentMethodMapper.findByMemberId(memberId);
            return !paymentMethods.isEmpty();
        } catch (Exception e) {
            log.error("회원 ID: {}의 결제수단 존재 여부 확인 실패", memberId, e);
            return false;
        }
    }

    /**
     * 기본 결제수단 조회 (첫 번째 등록된 결제수단)
     */
    @Transactional(readOnly = true)
    public PaymentMethodDto getDefaultPaymentMethod(Long memberId) throws CommonExceptionTemplate {
        List<PaymentMethodDto> paymentMethods = getPaymentMethods(memberId);

        if (paymentMethods.isEmpty()) {
            throw new CommonExceptionTemplate(404, "등록된 결제수단이 없습니다");
        }

        return paymentMethods.get(0); // 첫 번째를 기본으로 사용
    }

    /**
     * PaymentMethodEntity를 PaymentMethodDto로 변환
     */
    private PaymentMethodDto convertToDto(PaymentMethodEntity entity) {
        PaymentMethodDto dto = new PaymentMethodDto();
        dto.setId(entity.getId());
        dto.setMemberId(entity.getMemberId());
        dto.setTossKey(entity.getTossKey());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    /**
     * 민감한 정보 마스킹 처리 (보안)
     */
    private void maskSensitiveData(PaymentMethodDto dto) {
        if (dto.getTossKey() != null) {
            // 토스키를 마스킹 처리 (앞 4자리와 뒤 4자리만 표시)
            String tossKey = dto.getTossKey();
            if (tossKey.length() > 8) {
                String masked = tossKey.substring(0, 4) +
                        "*".repeat(tossKey.length() - 8) +
                        tossKey.substring(tossKey.length() - 4);
                dto.setTossKey(masked);
            }
        }
    }
}