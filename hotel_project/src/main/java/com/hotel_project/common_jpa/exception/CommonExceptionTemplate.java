package com.hotel_project.common_jpa.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommonExceptionTemplate extends Exception{

    private int code;
    private String message;

}
