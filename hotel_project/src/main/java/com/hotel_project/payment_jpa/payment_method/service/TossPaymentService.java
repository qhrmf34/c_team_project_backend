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

    // ===== 두 가지 시크릿 키 선언 =====
    @Value("${toss.payments.billing.secret-key}")
    private String billingSecretKey;  // 빌링키 발급용 (카드 등록)

    @Value("${toss.payments.widget.secret-key}")
    private String widgetSecretKey;   // 결제 처리용

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MemberMapper memberMapper;

    private static final String TOSS_BILLING_URL = "https://api.tosspayments.com/v1/billing/authorizations/card";
    private static final String TOSS_PAYMENT_CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
    /**
     * ===== 카드 등록 (빌링키 발급) =====
     * billingSecretKey 사용
     * 기존 코드와 동일하지만 키만 변경
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

            // billingSecretKey 사용
            String credentials = billingSecretKey + ":";
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

                if (response.isSuccessful()) {
                    return objectMapper.readValue(responseBody, TossBillingResponseDto.class);
                } else {
                    throw new CommonExceptionTemplate(response.code(), "토스 빌링키 등록 실패: " + responseBody);
                }
            }
        } catch (Exception e) {
            log.error("토스 빌링키 등록 중 오류", e);
            throw new CommonExceptionTemplate(500, "빌링키 등록 중 오류 발생");
        }
    }

    /**
     * ===== 빌링키로 결제 처리 (신규) =====
     * widgetSecretKey 사용
     * 등록된 빌링키를 사용해서 실제 결제
     */
    public TossPaymentResponseDto processPaymentWithBillingKey(
            String billingKey,
            Long amount,
            String orderId,
            String orderName,
            Long memberId) throws CommonExceptionTemplate {

        try {
            log.info("✅ 빌링키 결제 시작");
            log.info("빌링키: {}", billingKey);
            log.info("금액: {}", amount);
            log.info("주문ID: {}", orderId);

            // 1. 회원 정보 조회
            MemberDto member = memberMapper.findById(memberId);
            if (member == null) {
                throw new CommonExceptionTemplate(404, "회원 정보를 찾을 수 없습니다");
            }

            String customerName = getCustomerName(member);
            String customerEmail = member.getEmail();
            if (customerEmail == null || customerEmail.trim().isEmpty()) {
                customerEmail = "customer_" + memberId + "@hotel.com";
            }

            // 2. 결제 요청 데이터 구성
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("customerKey", "customer_" + memberId);
            requestData.put("amount", amount);
            requestData.put("orderId", orderId);
            requestData.put("orderName", orderName);
            requestData.put("customerEmail", customerEmail);
            requestData.put("customerName", customerName);

            String jsonBody = objectMapper.writeValueAsString(requestData);
            log.info("요청 데이터: {}", jsonBody);

            RequestBody body = RequestBody.create(
                    jsonBody, MediaType.get("application/json; charset=utf-8")
            );

            // ✅ 결제 처리용 Secret Key 사용
            String credentials = widgetSecretKey + ":";
            String basicAuth = Base64.getEncoder().encodeToString(credentials.getBytes());

            // 3. 빌링키로 결제 요청
            String paymentUrl = TOSS_PAYMENT_CONFIRM_URL + "/" + billingKey;
            log.info("결제 URL: {}", paymentUrl);

            Request httpRequest = new Request.Builder()
                    .url(paymentUrl)
                    .post(body)
                    .addHeader("Authorization", "Basic " + basicAuth)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Idempotency-Key", orderId)
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                String responseBody = response.body().string();

                log.info("✅ 토스 결제 API 응답 코드: {}", response.code());
                log.info("✅ 토스 결제 API 응답 본문: {}", responseBody);

                if (response.isSuccessful()) {
                    TossPaymentResponseDto paymentResponse =
                            objectMapper.readValue(responseBody, TossPaymentResponseDto.class);
                    log.info("✅✅ 토스 결제 성공! paymentKey: {}",
                            paymentResponse.getPaymentKey());
                    return paymentResponse;
                } else {
                    log.error("❌ 토스 결제 실패 - 코드: {}, 응답: {}",
                            response.code(), responseBody);
                    throw new CommonExceptionTemplate(
                            response.code(),
                            "토스 결제 실패: " + responseBody);
                }
            }

        } catch (IOException e) {
            log.error("❌ 토스 결제 API 호출 중 IO 오류", e);
            throw new CommonExceptionTemplate(500, "토스 결제 API 호출 중 오류 발생");
        } catch (CommonExceptionTemplate e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ 토스 결제 처리 중 예상치 못한 오류", e);
            throw new CommonExceptionTemplate(500, "결제 처리 중 오류 발생: " + e.getMessage());
        }
    }
    /**
     * ✅ 신규: 결제위젯 결제 승인
     * 프론트에서 결제 완료 후 백엔드에서 승인 처리
     */
    public TossPaymentResponseDto confirmWidgetPayment(
            String paymentKey,
            String orderId,
            Long amount) throws CommonExceptionTemplate {

        try {
            log.info("✅ 결제위젯 승인 시작");
            log.info("paymentKey: {}, orderId: {}, amount: {}", paymentKey, orderId, amount);

            // 승인 요청 데이터
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("paymentKey", paymentKey);
            requestData.put("orderId", orderId);
            requestData.put("amount", amount);

            String jsonBody = objectMapper.writeValueAsString(requestData);
            RequestBody body = RequestBody.create(
                    jsonBody, MediaType.get("application/json; charset=utf-8")
            );

            // ✅ widgetSecretKey 사용
            String credentials = widgetSecretKey + ":";
            String basicAuth = Base64.getEncoder().encodeToString(credentials.getBytes());

            Request httpRequest = new Request.Builder()
                    .url(TOSS_PAYMENT_CONFIRM_URL)
                    .post(body)
                    .addHeader("Authorization", "Basic " + basicAuth)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                String responseBody = response.body().string();

                log.info("✅ 토스 승인 응답 코드: {}", response.code());
                log.info("✅ 토스 승인 응답 본문: {}", responseBody);

                if (response.isSuccessful()) {
                    TossPaymentResponseDto paymentResponse =
                            objectMapper.readValue(responseBody, TossPaymentResponseDto.class);
                    log.info("✅✅ 결제 승인 성공!");
                    return paymentResponse;
                } else {
                    log.error("❌ 결제 승인 실패 - 코드: {}, 응답: {}", response.code(), responseBody);
                    throw new CommonExceptionTemplate(response.code(), "결제 승인 실패: " + responseBody);
                }
            }

        } catch (IOException e) {
            log.error("❌ 결제 승인 API 호출 중 IO 오류", e);
            throw new CommonExceptionTemplate(500, "결제 승인 API 호출 중 오류 발생");
        } catch (CommonExceptionTemplate e) {
            throw e;
        } catch (Exception e) {
            log.error("❌ 결제 승인 처리 중 예상치 못한 오류", e);
            throw new CommonExceptionTemplate(500, "결제 승인 중 오류 발생: " + e.getMessage());
        }
    }
    /**
     * 회원 이름 생성 헬퍼 메서드
     */
    private String getCustomerName(MemberDto member) {
        String provider = member.getProvider() != null ? member.getProvider().name() : "local";
        String firstName = member.getFirstName();
        String lastName = member.getLastName();

        if ("kakao".equals(provider) || "google".equals(provider) || "naver".equals(provider)) {
            if (firstName != null && !firstName.trim().isEmpty()) {
                return firstName;
            } else if (member.getEmail() != null) {
                return member.getEmail().split("@")[0];
            } else {
                return "소셜 회원";
            }
        }

        if ("local".equals(provider)) {
            if (firstName != null && lastName != null) {
                return firstName + " " + lastName;
            } else if (firstName != null) {
                return firstName;
            } else if (member.getEmail() != null) {
                return member.getEmail().split("@")[0];
            }
        }

        return "회원_" + member.getId();
    }
}