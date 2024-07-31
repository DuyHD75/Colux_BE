package com.dcode.identity_service.service.impl;

import com.dcode.identity_service.exception.ApiException;
import com.dcode.identity_service.service.IEmailService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static com.dcode.identity_service.constant.Constants.EmailSubjectConstant.NEW_USER_ACCOUNT_VERIFICATION;
import static com.dcode.identity_service.constant.Constants.EmailSubjectConstant.PASSWORD_RESET_REQUEST;
import static com.dcode.identity_service.utils.EmailUtils.getNewAccountEmailMessage;
import static com.dcode.identity_service.utils.EmailUtils.getResetPasswordEmailMessage;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.verify.host-server}")
    private String EMAIL_HOST_SERVER;

    @Value("${spring.mail.username}")
    private String SENDER_EMAIL;

    @Value("${spring.application.name}")
    private String SERVICE_NAME;

    @Value("${app.api-prefix}")
    private String API_PREFIX;

    @Override
    @Async
    public void sendNewAccountRegistrationEmail(String username,  String recipientEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
            message.setFrom(SENDER_EMAIL);
            message.setTo(recipientEmail);
            message.setText(getNewAccountEmailMessage(username, EMAIL_HOST_SERVER, SERVICE_NAME, API_PREFIX,  token));
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("Error sending email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String username, String recipientEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(PASSWORD_RESET_REQUEST);
            message.setFrom(SENDER_EMAIL);
            message.setTo(recipientEmail);
            message.setText(getResetPasswordEmailMessage(username, EMAIL_HOST_SERVER, SERVICE_NAME, API_PREFIX, token));
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiException("Error sending email: " + e.getMessage());
        }
    }
}
