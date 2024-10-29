package com.dcode.order_service.service.impl;

import com.dcode.order_service.config.PaypalConfig;
import com.dcode.order_service.config.PaypalHttpClient;
import com.dcode.order_service.dto.order.Order;
import com.dcode.order_service.dto.order.request.OrderRequest;
import com.dcode.order_service.dto.order.response.ConfirmedOrderResponse;
import com.dcode.order_service.dto.payment.PaypalRequest;
import com.dcode.order_service.dto.payment.PaypalResponse;
import com.dcode.order_service.dto.product.PurchaseResponse;
import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.entity.order.OrderLineEntity;
import com.dcode.order_service.enumuration.OrderStatus;
import com.dcode.order_service.enumuration.PaymentMethod;
import com.dcode.order_service.enumuration.payment.OrderIntent;
import com.dcode.order_service.enumuration.payment.PaymentLandingPage;
import com.dcode.order_service.exception.BusinessException;

import com.dcode.order_service.exception.ResourceNotFoundException;
import com.dcode.order_service.proxy.ICustomerClientProxy;
import com.dcode.order_service.proxy.ProductClientProxy;
import com.dcode.order_service.repository.ICartRepository;
import com.dcode.order_service.repository.IOrderLineRepository;
import com.dcode.order_service.repository.IOrderRepository;
import com.dcode.order_service.service.IOrderLineService;
import com.dcode.order_service.service.IOrderService;
import com.dcode.order_service.utils.OrderUtils;
import com.paypal.api.payments.Payment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static com.dcode.order_service.constant.Constants.AppConstants.*;
import static com.dcode.order_service.utils.OrderUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements IOrderService {

    private final ICustomerClientProxy clientProxy;
    private final ProductClientProxy productClientProxy;
    private final IOrderRepository orderRepository;
    private final IOrderLineRepository orderLineRepository;
    private final IOrderLineService orderLineService;
    private final PaypalConfig paypalConfig;
    private final ICartRepository cartRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PaypalHttpClient paypalHttpClient;

//    private final OrderProducer orderProducer;

    @Override
    public void cancelOrder(String code) {
        var order = orderRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException("Order not found"));

       if(order.getStatus() < 3) {
           order.setStatus(OrderStatus.CANCELLED.getValue());
           orderRepository.save(order);

           // Status 1 và 2 đang hàng đang được xử lý, có thể hủy
           // Thực hiện hủy đơn hàng trên GHN API


           // Publisher event



       }


        // Send notification
        // orderProducer.sendOrderStatus(order.getCode(), OrderStatus.CANCELLED.toString());
    }


    @Override
    public ConfirmedOrderResponse createClientOrder(OrderRequest request) {

        var purchaseProducts = productClientProxy.purchaseProducts(request.getPurchaseProducts());

        if (purchaseProducts.getStatus() != 200) {
            throw new BusinessException("Cannot create order :: Error purchasing products");
        }

        List<PurchaseResponse> purchaseResponses = purchaseProducts.getData();

        boolean isAllProductsPurchased = purchaseResponses.stream().allMatch(PurchaseResponse::getSuccess);

        if (!isAllProductsPurchased) {
            throw new BusinessException("Some products could not be processed: " + purchaseProducts.getMessage());
        }

        // Create order entity
        var orderEntity = mapToOrderEntity(request);
        orderEntity.setOrderLines(
                mapToOrderLineEntities(orderEntity, purchaseResponses)
        );

        BigDecimal totalAmount = calculateTotalAmount(orderEntity.getOrderLines());
        BigDecimal totalPay = calculateTotalPay(totalAmount, request.getShippingCost());

        orderEntity.setTotalAmount(totalAmount);
        orderEntity.setTax(totalAmount.multiply(BigDecimal.valueOf(0.1)));
        orderEntity.setTotalPay(totalPay);

        // Create confirmed order response
        ConfirmedOrderResponse confirmedOrderResponse = new ConfirmedOrderResponse();
        confirmedOrderResponse.setOrderCode(orderEntity.getCode());
        confirmedOrderResponse.setPaymentMethod(request.getPaymentMethod());


        if (request.getPaymentMethod() == PaymentMethod.CASH) {
            orderRepository.save(orderEntity);
        } else if (request.getPaymentMethod() == PaymentMethod.PAYPAL) {

            try {
                // if VN customer, convert to USD
                // BigDecimal totalPayInUSD = orderEntity.getTotalPay().divide(BigDecimal.valueOf(VND_TO_USD), 2, RoundingMode.HALF_UP);

                Payment payment = paypalConfig.createPayment(
                        orderEntity.getTotalPay().doubleValue(),
                        "USD",
                        PaymentMethod.PAYPAL.getValue(),
                        "sale",
                        "Order payment",
                        HOST_URL + SERVICE_NAME + "/api/v1/orders/payment/cancel",
                        HOST_URL + SERVICE_NAME + "/api/v1/orders/payment/success"
                );

                if (payment != null && payment.getState().equals("created")) {
                    orderEntity.setPaypalOrderId(payment.getId());
                    orderEntity.setPaypalOrderStatus(payment.getState());
                    orderRepository.save(orderEntity);

                    for (var link : payment.getLinks()) {
                        if (link.getRel().equals("approval_url")) {
                            confirmedOrderResponse.setOrderPaypalCheckoutLink(link.getHref());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error creating Paypal transaction: {}", e.getMessage());
                throw new BusinessException("Error creating Paypal transaction");
            }
        } else {
            throw new BusinessException("Invalid payment method");
        }

        return confirmedOrderResponse;
    }


    public List<OrderLineEntity> returnOrderToProductService(String orderId) {
        var orderLines = orderLineRepository.findByOrderEntity_orderId(orderId);
        if (orderLines.isEmpty()) {
            throw new BusinessException("No order lines found for order ID: " + orderId);
        }
        return orderLines;
    }

    @Override
    public void captureTransactionPaypal(String paypalOrderId, String payerId) throws ResourceNotFoundException {
        var order = orderRepository.findByPaypalOrderId(paypalOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER", "PAYPAL_ORDER_ID", paypalOrderId));

        try {
            order.setPaypalOrderStatus(OrderStatus.APPROVED.toString());
            // (1) Capture
            paypalHttpClient.capturePaypalTransaction(paypalOrderId, payerId);

            // (2) Cập nhật order
            order.setPaypalOrderStatus(OrderStatus.COMPLETED.toString());
            order.setPaymentStatus(2); // Status 2: Đã thanh toán

            // (3) Gửi notification


            // (4) Lưu order
            orderRepository.save(order);
        } catch (Exception e) {
            log.error("Cannot capture transaction: {0}", e);
        }

    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(OrderUtils::fromOrderEntity)
                .toList();
    }

    @Override
    public boolean hasCustomerPurchasedProduct(String customerId, String productId) {
        return orderLineRepository.existsByOrderEntity_customerIdAndProductId(customerId, productId);
    }
}
