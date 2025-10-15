package com.hotel_project.common_jpa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TurnstileService {

    private final WebClient.Builder webClientBuilder;

    @Value("${cloudflare.turnstile.secret-key}")
    private String secretKey;

    private static final String TURNSTILE_VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    /**
     * Turnstile 토큰 검증
     * @param token 프론트엔드에서 받은 Turnstile 토큰
     * @return 검증 성공 여부
     * @throws CommonExceptionTemplate 검증 실패 시
     */
    public boolean verifyToken(String token) throws CommonExceptionTemplate {
        if (token == null || token.trim().isEmpty()) {
            throw new CommonExceptionTemplate(400, "Turnstile 토큰이 제공되지 않았습니다.");
        }

        try {
            // 요청 바디 구성
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("secret", secretKey);
            requestBody.put("response", token);

            // Cloudflare Turnstile API 호출
            WebClient webClient = webClientBuilder.build();
            String responseJson = webClient.post()
                    .uri(TURNSTILE_VERIFY_URL)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> response = objectMapper.readValue(responseJson, Map.class);

            // 검증 결과 확인
            Boolean success = (Boolean) response.get("success");

            if (success == null || !success) {
                String[] errorCodes = response.get("error-codes") != null
                        ? ((java.util.List<String>) response.get("error-codes")).toArray(new String[0])
                        : new String[]{};

                throw new CommonExceptionTemplate(400,
                        "Turnstile 검증 실패: " + String.join(", ", errorCodes));
            }

            return true;

        } catch (CommonExceptionTemplate e) {
            throw e;
        } catch (Exception e) {
            throw new CommonExceptionTemplate(500, "Turnstile 검증 중 오류 발생: " + e.getMessage());
        }
    }
}