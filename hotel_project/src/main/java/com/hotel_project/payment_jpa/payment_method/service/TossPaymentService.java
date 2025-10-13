package com.hotel_project.payment_jpa.payment_method.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.member_jpa.member.mapper.MemberMapper;
import com.hotel_project.member_jpa.member.dto.MemberDto;
import com.hotel_project.payment_jpa.payment_method.dto.CardRegistrationRequestDto;
import com.hotel_project.payment_jpa.payment_method.dto.TossBillingResponseDto;
import com.hotel_project.payment_jpa.payment_method.dto.TossPaymentResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentService {

    @Value("${toss.payments.secret-key}")
    private String secretKey;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MemberMapper memberMapper;

    private static final String TOSS_BILLING_URL = "https://api.tosspayments.com/v1/billing/authorizations/card";
    private static final String TOSS_PAYMENT_URL = "https://api.tosspayments.com/v1/billing";

    /**
     * 빌링키 등록
     */
    public TossBillingResponseDto registerBillingKey(CardRegistrationRequestDto request, Long memberId) throws CommonExceptionTemplate {
        try {
            String customerKey = "customer_" + memberId;

            Map<String, Object> requestData = new HashMap<>();
            requestData.put("cardNumber", request.getCardNumber());
            requestData.put("cardExpirationYear", request.getCardExpirationYear());
            requestData.put("cardExpirationMonth", request.getCardExpirationMonth());
            requestData.put("cardPassword", request.getCardPassword());
            requestData.put("customerKey", customerKey);
            requestData.put("customerName", request.getCustomerName());

            String jsonBody = objectMapper.writeValueAsString(requestData);
            RequestBody body = RequestBody.create(
                    jsonBody, MediaType.get("application/json; charset=utf-8")
            );

            String credentials = secretKey + ":";
            String basicAuth = Base64.getEncoder().encodeToString(credentials.getBytes());

            Request httpRequest = new Request.Builder()
                    .url(TOSS_BILLING_URL)
                    .post(body)
                    .addHeader("Authorization", "Basic " + basicAuth)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                String responseBody = response.body().string();

                log.info("토스 빌링키 등록 API 응답 코드: {}", response.code());
                log.debug("토스 빌링키 등록 API 응답 본문: {}", responseBody);

                if (response.isSuccessful()) {
                    return objectMapper.readValue(responseBody, TossBillingResponseDto.class);
                } else {
                    log.error("토스 빌링키 등록 실패 - 코드: {}, 응답: {}", response.code(), responseBody);
                    throw new CommonExceptionTemplate(response.code(), "토스 빌링키 등록 실패: " + responseBody);
                }
            }

        } catch (IOException e) {
            log.error("토스 빌링키 등록 API 호출 중 IO 오류 발생", e);
            throw new CommonExceptionTemplate(500, "토스 빌링키 등록 API 호출 중 오류 발생");
        } catch (CommonExceptionTemplate e) {
            throw e;
        } catch (Exception e) {
            log.error("토스 빌링키 등록 중 예상치 못한 오류 발생", e);
            throw new CommonExceptionTemplate(500, "빌링키 등록 처리 중 오류 발생");
        }
    }

    /**
     * 빌링키를 사용한 실제 결제 처리
     */
    public TossPaymentResponseDto processPayment(String billingKey, Long amount, String orderId, String orderName, Long memberId) throws CommonExceptionTemplate {
        try {
            log.info("토스 결제 시작 - billingKey: {}, amount: {}, orderId: {}, memberId: {}", billingKey, amount, orderId, memberId);

            // 1. 회원 정보 조회
            MemberDto member = memberMapper.findById(memberId);
            if (member == null) {
                throw new CommonExceptionTemplate(404, "회원 정보를 찾을 수 없습니다");
            }

            // 2. 회원 이름 생성
            String customerName = getCustomerName(member);

            // 3. 회원 이메일
            String customerEmail = member.getEmail();
            if (customerEmail == null || customerEmail.trim().isEmpty()) {
                customerEmail = "customer_" + memberId + "@hotel.com";
            }

            // 4. 주문 ID 생성 (없으면 자동 생성)
            if (orderId == null || orderId.trim().isEmpty()) {
                orderId = "ORDER_" + System.currentTimeMillis() + "_" + memberId;
            }

            // 5. 주문명 설정 (없으면 기본값)
            if (orderName == null || orderName.trim().isEmpty()) {
                orderName = "호텔 예약 결제";
            }

            // 6. 토스 결제 요청 데이터 구성
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("customerKey", "customer_" + memberId);
            requestData.put("amount", amount);
            requestData.put("orderId", orderId);
            requestData.put("orderName", orderName);
            requestData.put("customerEmail", customerEmail);
            requestData.put("customerName", customerName);

            // 7. HTTP 요청 생성
            String jsonBody = objectMapper.writeValueAsString(requestData);
            RequestBody body = RequestBody.create(
                    jsonBody, MediaType.get("application/json; charset=utf-8")
            );

            // 8. Basic Auth 헤더 생성
            String credentials = secretKey + ":";
            String basicAuth = Base64.getEncoder().encodeToString(credentials.getBytes());

            // 9. 빌링키를 사용한 결제 요청
            String paymentUrl = TOSS_PAYMENT_URL + "/" + billingKey;
            Request httpRequest = new Request.Builder()
                    .url(paymentUrl)
                    .post(body)
                    .addHeader("Authorization", "Basic " + basicAuth)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Idempotency-Key", orderId) // 중복 결제 방지
                    .build();

            // 10. API 호출 및 응답 처리
            try (Response response = client.newCall(httpRequest).execute()) {
                String responseBody = response.body().string();

                log.info("토스 결제 API 응답 코드: {}", response.code());
                log.debug("토스 결제 API 응답 본문: {}", responseBody);

                if (response.isSuccessful()) {
                    TossPaymentResponseDto paymentResponse = objectMapper.readValue(responseBody, TossPaymentResponseDto.class);
                    log.info("토스 결제 성공 - paymentKey: {}, amount: {}", paymentResponse.getPaymentKey(), paymentResponse.getTotalAmount());
                    return paymentResponse;
                } else {
                    log.error("토스 결제 실패 - 코드: {}, 응답: {}", response.code(), responseBody);
                    throw new CommonExceptionTemplate(response.code(), "토스 결제 실패: " + responseBody);
                }
            }

        } catch (IOException e) {
            log.error("토스 결제 API 호출 중 IO 오류 발생", e);
            throw new CommonExceptionTemplate(500, "토스 결제 API 호출 중 오류 발생");
        } catch (CommonExceptionTemplate e) {
            throw e;
        } catch (Exception e) {
            log.error("토스 결제 처리 중 예상치 못한 오류 발생", e);
            throw new CommonExceptionTemplate(500, "결제 처리 중 오류 발생");
        }
    }

    /**
     * 회원 이름 생성 (소셜 로그인 처리 포함)
     */
    private String getCustomerName(MemberDto member) {
        String provider = member.getProvider() != null ? member.getProvider().name() : "local";
        String firstName = member.getFirstName();
        String lastName = member.getLastName();

        // 소셜 로그인인 경우 (kakao, google, naver)
        if ("kakao".equals(provider) || "google".equals(provider) || "naver".equals(provider)) {
            if (firstName != null && !firstName.trim().isEmpty()) {
                return firstName;
            } else if (member.getEmail() != null) {
                return member.getEmail().split("@")[0];
            } else {
                return "소셜 회원";
            }
        }

        // local 로그인인 경우
        if ("local".equals(provider)) {
            if (firstName != null && lastName != null) {
                return firstName + " " + lastName;
            } else if (firstName != null) {
                return firstName;
            } else if (member.getEmail() != null) {
                return member.getEmail().split("@")[0];
            }
        }

        // 기본값
        return "회원_" + member.getId();
    }
}