package com.hotel_project.common_jpa.controller.addvice;

import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class MemberExceptionAdvice {

    @ExceptionHandler(CommonExceptionTemplate.class)
    public ResponseEntity<Map<String,Object>> handlerMemberException(CommonExceptionTemplate ex){
        int code=ex.getCode();
        String message=ex.getMessage();

        return ResponseEntity.badRequest().body(Map.of("code",code,"message",message));
    }

}
