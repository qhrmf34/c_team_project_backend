package com.hotel_project.common_jpa.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(int code, String message, T data) {
        return new ApiResponse(code,message,data);
    }

    public static <T>ApiResponse<T> error(int code, String message, T data) {
        return new ApiResponse<T>(code,message, null);
    }

}
