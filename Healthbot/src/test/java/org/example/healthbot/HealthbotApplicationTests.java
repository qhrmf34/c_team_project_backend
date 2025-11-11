//package org.example.healthbot;
//
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestClientException;
//import org.springframework.web.client.RestTemplate;
//
//@SpringBootApplication
//@EnableScheduling
//public class HealthbotApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(HealthbotApplication.class, args);
//    }
//
//    @Component
//    class WebhookTask {
//        private final RestTemplate restTemplate = new RestTemplate();
//
//        private final String webhookUrl = "웹훅주소";
//
//        private final String targetUrl = "http://localhost:8086";
//
//        @Scheduled(fixedRate = 3600000)
//        public void checkHealthAndNotify() {
//
//            try {
//                restTemplate.getForObject((String) targetUrl, String.class);
//                System.out.println("✅ 서버살아있다...: " + targetUrl);
//            } catch (RestClientException e) {
//                sendWebhook("🚨 ALERT: 서버 응답없음! 확인 필요!\n```\n" + e.getMessage() + "\n```",
//                        (String) webhookUrl);
//            }
//
//        }
//
//        private void sendWebhook(String message, String url) {
//            try {
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.APPLICATION_JSON);
//
//                Map<String, String> payload = new HashMap<>();
//                payload.put("content", message);
//
//                HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);
//                String response = restTemplate.postForObject(url, request, String.class);
//                System.out.println("Webhook response: " + response);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
//
