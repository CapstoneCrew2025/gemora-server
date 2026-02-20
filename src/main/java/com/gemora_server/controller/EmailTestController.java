package com.gemora_server.controller;

import com.gemora_server.service.EmailService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class EmailTestController {

    private final EmailService emailService;

    public EmailTestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/email")
    public String testEmail() {

        emailService.sendOtpEmail(
                "binojmadhuranga2002.04.04@gmail.com",
                "123456"
        );

        return "Test email sent successfully!";
    }
}