package com.hotel_project.hotel_jpa.freebies.controller;


import com.hotel_project.common_jpa.exception.CommonExceptionTemplate;
import com.hotel_project.common_jpa.exception.MemberException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test(@RequestParam String 아무거나) throws CommonExceptionTemplate {
        throw MemberException.NOT_EXIST_MEMBERID.getException();
    }

    @GetMapping("test2")
    public String test2(){
        return "안녕";
    }

}
