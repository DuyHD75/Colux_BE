package com.dcode.order_service.service.impl;

import com.dcode.order_service.config.PaypalConfig;
import com.dcode.order_service.dto.order.Order;
import com.dcode.order_service.dto.order.request.OrderLineRequest;
import com.dcode.order_service.dto.order.request.OrderRequest;
import com.dcode.order_service.dto.order.response.ConfirmedOrderResponse;
import com.dcode.order_service.dto.product.PurchaseRequest;
import com.dcode.order_service.dto.product.PurchaseResponse;
import com.dcode.order_service.dto.product.PurchaseResponseWrapper;
import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.entity.order.OrderLineEntity;
import com.dcode.order_service.enumuration.PaymentMethod;
import com.dcode.order_service.exception.BusinessException;

import com.dcode.order_service.proxy.ICustomerClientProxy;
import com.dcode.order_service.proxy.ProductClientProxy;
import com.dcode.order_service.repository.IOrderLineRepository;
import com.dcode.order_service.repository.IOrderRepository;
import com.dcode.order_service.service.IOrderLineService;
import com.dcode.order_service.service.IOrderService;
import com.dcode.order_service.utils.OrderUtils;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.ProducerRecord;

import static com.dcode.order_service.constant.Constants.AppConstants.HOST_URL;
import static com.dcode.order_service.utils.OrderUtils.createNewOrderEntity;

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
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final int VND_TO_USD = 23_000;

//    private final OrderProducer orderProducer;

    @Override
    public void cancelOrder(String code) {
//     This method is not implemented yet
    }

    @Override
    public ConfirmedOrderResponse createClientOrder(OrderRequest request) {
        var customer = this.clientProxy.findUserByUserId(request.getCustomerId())
                .orElseThrow(() -> new BusinessException("Cannot create order :: No customer found with ID: " + request.getCustomerId()));

        log.info("Customer found: {}", customer);
        log.info("Sending purchase order request: {}", request.getPurchaseProducts());
        PurchaseResponseWrapper purchaseResponseWrapper = productClientProxy.purchaseProducts(request.getPurchaseProducts());
        // create order confirmation
        ConfirmedOrderResponse confirmedOrderResponse = new ConfirmedOrderResponse();

        if (purchaseResponseWrapper.getStatus() == 200) {
            List<PurchaseResponse> purchasedProducts = purchaseResponseWrapper.getData();

            boolean allProductsSuccessful = purchasedProducts.stream().allMatch(PurchaseResponse::getSuccess);

            if (!allProductsSuccessful) {
                // Return the response from product-service directly
                throw new BusinessException("Some products could not be processed: " + purchaseResponseWrapper.getMessage());
            }

            // Create a map to map between request and response to create orderLine below
            Map<String, PurchaseResponse> responseMap = purchasedProducts.stream()
                    .collect(Collectors.toMap(
                            response -> {
                                if (response.getPaintId() != null) return response.getPaintId();
                                if (response.getWallpaperId() != null) return response.getWallpaperId();
                                if (response.getFloorId() != null) return response.getFloorId();
                                return null;
                            },
                            response -> response
                    ));

            var order = this.orderRepository.save(createNewOrderEntity(request));

            for (PurchaseRequest purchaseRequest : request.getPurchaseProducts()) {
                String key = purchaseRequest.paintId() != null ? purchaseRequest.paintId() :
                        purchaseRequest.wallpaperId() != null ? purchaseRequest.wallpaperId() :
                                purchaseRequest.floorId();

                PurchaseResponse correspondingResponse = responseMap.get(key);

                if (correspondingResponse != null) {
                    orderLineService.saveOrderLine(
                            new OrderLineRequest(
                                    order.getOrderId(),
                                    purchaseRequest.productId(),
                                    purchaseRequest.quantity(),
                                    correspondingResponse.getPrice(),
                                    purchaseRequest.variantId(),
                                    purchaseRequest.paintId(),
                                    purchaseRequest.wallpaperId(),
                                    purchaseRequest.floorId()
                            )
                    );
                } else {
                    log.warn("No corresponding response found for productId: {}", purchaseRequest.productId());
                }
            }

            // Gửi message tới Kafka với order ID ngay khi tạo
            ProducerRecord<String, String> record = new ProducerRecord<>("order-check-status", order.getOrderId());
            kafkaTemplate.send(record);


            confirmedOrderResponse.setOrderCode(order.getOrderId());
            if (request.getPaymentMethod() == PaymentMethod.CASH) {
                orderRepository.save(order);
                confirmedOrderResponse.setMessage(PaymentMethod.CASH);
            } else if (request.getPaymentMethod() == PaymentMethod.PAYPAL) {
                confirmedOrderResponse.setMessage(PaymentMethod.PAYPAL);
                try {
                    BigDecimal totalPay = request.getTotalPay();
                    if (totalPay.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new BusinessException("Total payment amount must be greater than zero");
                    }

                    BigDecimal convertTotalPayUSD = totalPay.divide(BigDecimal.valueOf(VND_TO_USD), 2, BigDecimal.ROUND_HALF_UP);

                    Payment payment = paypalConfig.createPayment(
                            convertTotalPayUSD.doubleValue(),
                            "USD",
                            "paypal",
                            "sale",
                            "Order payment",
                            HOST_URL + "/api/v1/orders/paypal/cancel",
                            HOST_URL + "/api/v1/orders/paypal/capture"
                    );

                    // Handle PayPal payment processing here
                    if (payment != null && payment.getState().equals("created")) {
                        String approvalUrl = payment.getLinks().stream()
                                .filter(link -> link.getRel().equals("approval_url"))
                                .findFirst()
                                .orElseThrow(() -> new BusinessException("No approval URL found in PayPal response"))
                                .getHref();

                        log.info("Redirecting user to PayPal for payment approval: {}", approvalUrl);

                        // Return the approval URL to the client
                        confirmedOrderResponse.setOrderPaypalCheckoutLink(approvalUrl);
                    } else {
                        throw new BusinessException("Error while creating PayPal payment");
                    }

                } catch (PayPalRESTException e) {
                    log.error("Error while processing payment", e);
                    throw new BusinessException("Error while processing payment");
                }
            }

        } else if (purchaseResponseWrapper.getStatus() == 400) {
            // Return the response from product-service directly
            throw new BusinessException("Product purchase failed: " + purchaseResponseWrapper.getMessage(), purchaseResponseWrapper);
        } else {
            throw new BusinessException("Unexpected error while purchasing products");
        }
        return confirmedOrderResponse;
    }
    //        orderProducer.sendOrderConfirmation(
//                new OrderConfirmation(
//                        request.getReference(),
//                        request.getTotalPay(),
//                        request.getPaymentMethod(),
//                        customer,
//                        purchasedProducts
//                )
//        );

    public List<OrderLineEntity> returnOrderToProductService(String orderId) {
        var orderLines = orderLineRepository.findByOrderEntity_orderId(orderId);
        if (orderLines.isEmpty()) {
            throw new BusinessException("No order lines found for order ID: " + orderId);
        }
        return orderLines;
    }

    @Override
    public void captureTransactionPaypal(String paypalOrderId, String payerId) {
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
