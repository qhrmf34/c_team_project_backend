package com.hotel_project.common_jpa.controller.addvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel_project.common_jpa.util.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Map;

@RestControllerAdvice
public class CommonResponseController implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // Swagger 관련 요청은 제외
        String packageName = returnType.getContainingClass().getPackage().getName();

        // Swagger/OpenAPI 관련 패키지들 제외
        if (packageName.contains("springdoc") ||
                packageName.contains("swagger") ||
                packageName.contains("openapi")) {
            return false;
        }

        // 우리 API만 처리
        return packageName.startsWith("com.hotel_project");
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // Already wrapped → skip
        if (body instanceof ApiResponse)
            return body;
        // **에러 응답은 래핑하지 않음**
        if (body instanceof Map) {
            Map<String, Object> mapBody = (Map<String, Object>) body;
            if (mapBody.containsKey("code") && mapBody.containsKey("message")) {
                return body; // 에러 응답은 그대로 반환
            }
        }

        // Handle String specially (Spring uses StringHttpMessageConverter)
        if (body instanceof String) {
            try {
                return new ObjectMapper().writeValueAsString(ApiResponse.success(200,"success",body));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // Default: wrap in ApiResponse
        return ApiResponse.success(200, "success", body);
    }
}