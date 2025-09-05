package com.hotel_project.common_jpa;


import com.hotel_project.common_jpa.dto.ResponseCode;
import com.hotel_project.common_jpa.dto.ResponseDto;
import org.springframework.http.ResponseEntity;

public class CommonRestController {
    public ResponseEntity<ResponseDto> getReponseEntity(ResponseCode code, String message, Object data, Throwable th) {
        if ( th == null ) {
            return ResponseEntity.ok().body(
                    ResponseDto.builder()
                            .code(code)
                            .message(message)
                            .responseData(data)
                            .build()
            );
        } else {
            return ResponseEntity.status(500).body(
                    ResponseDto.builder()
                    .code(code)
                    .message(message)
                    .responseData(data)
                    .build()
            );
        }
    }
}
