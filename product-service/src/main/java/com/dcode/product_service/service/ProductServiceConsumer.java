package com.dcode.product_service.service;


import com.dcode.product_service.dtoRequest.order_service.OrderLineDTO;
import com.dcode.product_service.dtoRequest.order_service.RestoreMessageDTO;
import com.dcode.product_service.service.impl.ProductServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@AllArgsConstructor
@Slf4j
public class ProductServiceConsumer {
    private final ObjectMapper objectMapper;
    private final ProductServiceImpl productService;

    @KafkaListener(topics = "order-cancel-restore", groupId = "product-consumer-group")
    public void restoreInventory(String restoreMessage) {
        log.info("Received restore message: " + restoreMessage);
        try {
            List<OrderLineDTO> orderLines = objectMapper.readValue(restoreMessage, new TypeReference<List<OrderLineDTO>>() {});
            // Update inventory using orderLines
            productService.orderCancelRestore(orderLines);
            log.info("Parsed restore message: " + orderLines);
        } catch (Exception e) {
            log.error("Failed to parse restore message", e);
        }
    }
}