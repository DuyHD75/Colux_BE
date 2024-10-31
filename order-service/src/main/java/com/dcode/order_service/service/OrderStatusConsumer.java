package com.dcode.order_service.service;

import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.exception.BusinessException;
import com.dcode.order_service.repository.IOrderRepository;
import com.dcode.order_service.service.impl.OrderServiceImpl;
import com.dcode.order_service.utils.OrderUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.dcode.order_service.utils.OrderUtils.fromOrderLineEntity;

@Service
@AllArgsConstructor
@Slf4j
public class OrderStatusConsumer {
    private final IOrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); // Scheduler for delay
    private final OrderServiceImpl orderServiceImpl;
    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, Integer> retryCounts = new ConcurrentHashMap<>();

    @KafkaListener(topics = "order-check-status", groupId = "order-consumer-group", concurrency = "3")
    public void checkOrderStatus(String orderId, Acknowledgment acknowledgment) {
        log.info("Received order ID: " + orderId);
        Optional<OrderEntity> orderOpt = orderRepository.findByOrderId(orderId);

        if (orderOpt.isPresent()) {
            OrderEntity order = orderOpt.get();
            log.info("Processing order ID: " + orderId + " with status: " + order.getStatus());

            if (order.getPaymentStatus() == 2) {
                log.info("Order ID: " + orderId + " is already paid. No need to check status");
                acknowledgment.acknowledge();
                return;
            }

            LocalDateTime orderCreatedAt = order.getCreatedAt();
            if (orderCreatedAt.plusSeconds(10).isBefore(LocalDateTime.now())) {
                order.setStatus(3); // CANCELLED
                orderRepository.save(order);

                log.info("Order ID: " + orderId + " is out of time. Sending restore message to ProductService");
                var orderLines = orderServiceImpl.returnOrderToProductService(order.getOrderId());
                // Convert list of order lines to JSON format
                var orderLineResponse = orderLines.stream().map(OrderUtils::fromOrderLineEntity);
                String restoreMessage = null;
                try {
                    restoreMessage = objectMapper.writeValueAsString(orderLineResponse);
                } catch (JsonProcessingException e) {
                    log.error("Cannot parse order lines to JSON format", e);
                    throw new BusinessException("Cannot parse order lines to JSON format", e);
                }
                // Send to Kafka topic
                kafkaTemplate.send("order-cancel-restore", restoreMessage);
            } else {
                log.info("Order ID: " + orderId + " is still in time. Scheduling check again in 15 seconds");
                scheduler.schedule(() -> kafkaTemplate.send("order-check-status", orderId), 5, TimeUnit.SECONDS);
            }
        } else {
            int retryCount = retryCounts.getOrDefault(orderId, 0);
            if (retryCount < 5) {
                log.warn("Order ID: " + orderId + " not found. Retry count: " + retryCount);
                retryCounts.put(orderId, retryCount + 1);
                scheduler.schedule(() -> kafkaTemplate.send("order-check-status", orderId), 5, TimeUnit.SECONDS);
            } else {
                log.warn("Order ID: " + orderId + " not found after 5 retries. Giving up.");
                retryCounts.remove(orderId);
            }
        }
        acknowledgment.acknowledge();
    }

    private String createRestoreMessage(OrderEntity order) {
        // Create message with product list and quantity for ProductService to handle
        return "Restore items for order ID: " + order.getId();
    }
}