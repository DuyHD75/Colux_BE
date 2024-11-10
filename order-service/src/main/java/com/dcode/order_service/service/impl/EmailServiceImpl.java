package com.dcode.order_service.service.impl;


import com.dcode.order_service.dto.cart.response.CartVariantResponse;
import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.exception.ApiException;
import com.dcode.order_service.service.IEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.dcode.order_service.constant.Constants.EmailSubjectConstant.*;
import static com.dcode.order_service.utils.EmailTemplateUtils.*;


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
    public void sendOrderPlacedEmail(OrderEntity orderEntity, List<CartVariantResponse.ClientVariantResponse> productLines, Map<?, ?> data) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(CONFIRM_PLACED_ORDER);
            message.setFrom(SENDER_EMAIL);
            message.setTo((String) data.get("email"));
            message.setText(createOrderConfirmationEmail(orderEntity,productLines, data));
            javaMailSender.send(message);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("Error sending email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendOrderCancelledEmail(OrderEntity orderEntity, List<CartVariantResponse.ClientVariantResponse> productLines, Map<?, ?> data) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(CONFIRM_CANCELED_ORDER);
            message.setFrom(SENDER_EMAIL);
            message.setTo((String) data.get("email"));
            message.setText(createOrderCancellationEmail(orderEntity,productLines, data));
            javaMailSender.send(message);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("Error sending email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendOrderCompletedEmail(OrderEntity orderEntity, List<CartVariantResponse.ClientVariantResponse> productLines, Map<?, ?> data) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setSubject(CONFIRM_COMPLETED_ORDER);
            message.setFrom(SENDER_EMAIL);
            message.setTo((String) data.get("email"));
            message.setText(createOrderCompletionEmail(orderEntity,productLines, data));
            javaMailSender.send(message);
        }catch (Exception e){
            log.error(e.getMessage());
            throw new ApiException("Error sending email: " + e.getMessage());
        }
    }

}
