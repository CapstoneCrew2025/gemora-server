package com.gemora_server.service;

public interface EmailService {

    void sendOtpEmail(String to, String otp);

}