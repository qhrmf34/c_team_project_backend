package com.hotel_project.common_jpa.config;

import com.hotel_project.member_jpa.mail_authentication.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Autowired
    private EmailService emailService;

    @Scheduled(fixedRate = 3600000) // 1시간마다
    public void cleanupExpiredVerificationCodes() {
        emailService.cleanupExpiredCodes();
    }
}