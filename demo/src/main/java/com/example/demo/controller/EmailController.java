package com.example.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.demo.mailSample;
import com.example.demo.service.EmailService;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendEmail")
    public String sendEmail(@RequestBody mailSample emailRequest) {
        try {
            emailService.sendTemplateEmail(
                emailRequest.getTo(),
                emailRequest.getSubject(),
                emailRequest.getName(),
                emailRequest.getMessage()
            );
            return "Email sent successfully!";
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }
}
