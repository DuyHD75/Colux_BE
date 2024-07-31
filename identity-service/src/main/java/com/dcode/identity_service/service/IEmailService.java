package com.dcode.identity_service.service;

public interface IEmailService {
    void sendNewAccountRegistrationEmail(String name, String addressRecipient, String token);
    void sendPasswordResetEmail(String name, String addressRecipient, String token);
}
