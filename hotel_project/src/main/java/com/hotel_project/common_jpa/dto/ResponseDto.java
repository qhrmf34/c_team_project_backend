package com.hotel_project.common_jpa.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto {
    private ResponseCode code;
    private String message;
    private Object responseData;
}
