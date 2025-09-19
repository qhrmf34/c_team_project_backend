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

@RestControllerAdvice
public class CommonResponseController implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // Already wrapped → skip
        if (body instanceof ApiResponse)
            return body;

        // Handle String specially (Spring uses StringHttpMessageConverter)
        if (body instanceof String) {
            try {
                return new ObjectMapper().writeValueAsString(ApiResponse.success(200,"success",body));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // Default: wrap in ApiResponse
        return ApiResponse.success(0,null,null);
    }
}
