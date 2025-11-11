package org.example.securitytest4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
public class SecurityTest4Application {

    public static void main(String[] args) {
        SpringApplication.run(SecurityTest4Application.class, args);
    }

    @RestController
    class TestController {
        @GetMapping("/test")
        public String test(){
            return "test...";
        }
    }
}
