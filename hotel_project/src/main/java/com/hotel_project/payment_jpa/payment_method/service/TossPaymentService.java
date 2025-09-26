package com.hotel_project.payment_jpa.payment_method.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel_project.payment_jpa.payment_method.dto.CardRegistrationRequestDto;
import com.hotel_project.payment_jpa.payment_method.dto.TossBillingResponseDto;

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
public class TossPaymentService {

    @Value("${toss.payments.secret-key}")
    private String secretKey;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TOSS_BILLING_URL = "https://api.tosspayments.com/v1/billing/authorizations/card";

    /**
     * 빌링키 등록 (카드 정보로 토스에 빌링키 요청)
     */
    public TossBillingResponseDto registerBillingKey(CardRegistrationRequestDto request, Long memberId) {
        try {
            // 1. 고객 키 생성 (회원 ID 기반)
            String customerKey = "customer_" + memberId;

            // 2. 토스 API 요청 데이터 구성
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("cardNumber", request.getCardNumber());
            requestData.put("cardExpirationYear", request.getCardExpirationYear());
            requestData.put("cardExpirationMonth", request.getCardExpirationMonth());
            requestData.put("cardPassword", request.getCardPassword());
            requestData.put("customerKey", customerKey);
            requestData.put("customerName", request.getCustomerName());
            

            // 3. HTTP 요청 생성
            String jsonBody = objectMapper.writeValueAsString(requestData);
            RequestBody body = RequestBody.create(
                    jsonBody, MediaType.get("application/json; charset=utf-8")
            );

            // 4. Basic Auth 헤더 생성
            String credentials = secretKey + ":";
            String basicAuth = Base64.getEncoder().encodeToString(credentials.getBytes());

            Request httpRequest = new Request.Builder()
                    .url(TOSS_BILLING_URL)
                    .post(body)
                    .addHeader("Authorization", "Basic " + basicAuth)
                    .addHeader("Content-Type", "application/json")
                    .build();

            // 5. API 호출 및 응답 처리
            try (Response response = client.newCall(httpRequest).execute()) {
                String responseBody = response.body().string();

                log.info("토스 빌링키 등록 API 응답 코드: {}", response.code());
                log.debug("토스 빌링키 등록 API 응답 본문: {}", responseBody);

                if (response.isSuccessful()) {
                    return objectMapper.readValue(responseBody, TossBillingResponseDto.class);
                } else {
                    log.error("토스 빌링키 등록 실패 - 코드: {}, 응답: {}", response.code(), responseBody);
                    throw new RuntimeException("토스 빌링키 등록 실패: " + responseBody);
                }
            }

        } catch (IOException e) {
            log.error("토스 빌링키 등록 API 호출 중 IO 오류 발생", e);
            throw new RuntimeException("토스 빌링키 등록 API 호출 중 오류 발생", e);
        } catch (Exception e) {
            log.error("토스 빌링키 등록 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("빌링키 등록 처리 중 오류 발생", e);
        }
    }
    // TossPaymentService.java에 추가할 메서드

//    /**
//     * 빌링키를 사용한 결제 처리
//     */
//    public TossPaymentResponseDto processPayment(String billingKey, PaymentRequestDto request) {
//        try {
//            // 1. 주문 ID 생성 (없으면 자동 생성)
//            String orderId = request.getOrderId();
//            if (orderId == null || orderId.trim().isEmpty()) {
//                orderId = "ORDER_" + System.currentTimeMillis() + "_" + request.getReservationsId();
//            }
//
//            // 2. 주문명 설정 (없으면 기본값)
//            String orderName = request.getOrderName();
//            if (orderName == null || orderName.trim().isEmpty()) {
//                orderName = "호텔 예약 결제";
//            }
//
//            // 3. 토스 결제 요청 데이터 구성
//            Map<String, Object> requestData = new HashMap<>();
//            requestData.put("customerKey", "customer_" + request.getReservationsId());
//            requestData.put("amount", request.getPaymentAmount());
//            requestData.put("orderId", orderId);
//            requestData.put("orderName", orderName);
//            requestData.put("customerEmail", "customer@hotel.com");
//            requestData.put("customerName", "고객명");
//
//            // 4. HTTP 요청 생성
//            String jsonBody = objectMapper.writeValueAsString(requestData);
//            RequestBody body = RequestBody.create(
//                    jsonBody, MediaType.get("application/json; charset=utf-8")
//            );
//
//            // 5. Basic Auth 헤더 생성
//            String credentials = secretKey + ":";
//            String basicAuth = Base64.getEncoder().encodeToString(credentials.getBytes());
//
//            // 6. 빌링키를 사용한 결제 요청
//            String paymentUrl = "https://api.tosspayments.com/v1/billing/" + billingKey;
//            Request httpRequest = new Request.Builder()
//                    .url(paymentUrl)
//                    .post(body)
//                    .addHeader("Authorization", "Basic " + basicAuth)
//                    .addHeader("Content-Type", "application/json")
//                    .build();
//
//            // 7. API 호출 및 응답 처리
//            try (Response response = client.newCall(httpRequest).execute()) {
//                String responseBody = response.body().string();
//
//                log.info("토스 결제 API 응답 코드: {}", response.code());
//                log.debug("토스 결제 API 응답 본문: {}", responseBody);
//
//                if (response.isSuccessful()) {
//                    return objectMapper.readValue(responseBody, TossPaymentResponseDto.class);
//                } else {
//                    log.error("토스 결제 처리 실패 - 코드: {}, 응답: {}", response.code(), responseBody);
//                    throw new RuntimeException("토스 결제 처리 실패: " + responseBody);
//                }
//            }
//
//        } catch (IOException e) {
//            log.error("토스 결제 API 호출 중 IO 오류 발생", e);
//            throw new RuntimeException("토스 결제 API 호출 중 오류 발생", e);
//        } catch (Exception e) {
//            log.error("토스 결제 처리 중 예상치 못한 오류 발생", e);
//            throw new RuntimeException("결제 처리 중 오류 발생", e);
//        }
//    }
}

