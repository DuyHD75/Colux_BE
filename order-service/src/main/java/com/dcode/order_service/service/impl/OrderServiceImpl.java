package com.dcode.order_service.service.impl;

import com.dcode.order_service.config.PaypalConfig;
import com.dcode.order_service.config.PaypalHttpClient;
import com.dcode.order_service.dto.cart.request.CartVariantKeyRequest;
import com.dcode.order_service.dto.cart.request.CartVariantRequest;
import com.dcode.order_service.dto.cart.response.CartVariantResponse;
import com.dcode.order_service.dto.order.Order;
import com.dcode.order_service.dto.order.request.GhnCalculateFeeRequest;
import com.dcode.order_service.dto.order.request.OrderRequest;
import com.dcode.order_service.dto.order.response.ConfirmedOrderResponse;
import com.dcode.order_service.dto.order.response.GhnCalculateFeeResponse;
import com.dcode.order_service.dto.payment.PaypalRequest;
import com.dcode.order_service.dto.payment.PaypalResponse;
import com.dcode.order_service.dto.product.PurchaseRequest;
import com.dcode.order_service.dto.product.PurchaseResponse;
import com.dcode.order_service.entity.cart.CartEntity;
import com.dcode.order_service.entity.cart.CartVariantEntity;
import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.entity.order.OrderLineEntity;
import com.dcode.order_service.entity.waybill.Waybill;
import com.dcode.order_service.enumuration.EventType;
import com.dcode.order_service.enumuration.OrderStatus;
import com.dcode.order_service.enumuration.PaymentMethod;
import com.dcode.order_service.enumuration.payment.OrderIntent;
import com.dcode.order_service.enumuration.payment.PaymentLandingPage;
import com.dcode.order_service.event.OrderEvent;
import com.dcode.order_service.exception.BusinessException;

import com.dcode.order_service.exception.ResourceNotFoundException;
import com.dcode.order_service.proxy.ICustomerClientProxy;
import com.dcode.order_service.proxy.ProductClientProxy;
import com.dcode.order_service.repository.*;
import com.dcode.order_service.service.ICartService;
import com.dcode.order_service.service.IOrderLineService;
import com.dcode.order_service.service.IOrderService;
import com.dcode.order_service.utils.OrderUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.paypal.api.payments.Payment;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
//import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
    private final IWaybillRepository waybillRepository;
    private final ApplicationEventPublisher publisher;
    private final ICartService cartService;


    @Value("${spring.shipping.ghnToken}")
    private String ghnToken;
    @Value("${spring.shipping.ghnShopId}")
    private String ghnShopId;
    @Value("${spring.shipping.ghnApiPath}")
    private String ghnApiPath;

//    private final OrderProducer orderProducer;

    @Override
    public void cancelOrder(String code) {
        OrderEntity order = orderRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException("Order not found with code: " + code));

        // Hủy đơn hàng khi status = 1 hoặc 2
        if (order.getStatus() < 3) {
            order.setStatus(5); // Status 5 là trạng thái Hủy
            orderRepository.save(order);

            sendEmail(order, EventType.ORDER_CANCELLED);

            Waybill waybill = waybillRepository.findByOrder_OrderId(order.getOrderId()).orElse(null);

            // Status 1 là Vận đơn đang chờ lấy hàng
            /*if (waybill != null && waybill.getStatus() == 1) {
                String cancelOrderApiPath = ghnApiPath + "/switch-status/cancel";

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Token", ghnToken);
                headers.add("ShopId", ghnShopId);

                RestTemplate restTemplate = new RestTemplate();

                var request = new HttpEntity<>(new GhnCancelOrderRequest(List.of(waybill.getCode())), headers);
                var response = restTemplate.postForEntity(cancelOrderApiPath, request, GhnCancelOrderResponse.class);

                if (response.getStatusCode() != HttpStatus.OK) {
                    throw new RuntimeException("Error when calling Cancel Order GHN API");
                }

                // Integrated with GHN API
                if (response.getBody() != null) {
                    for (var data : response.getBody().getData()) {
                        if (data.getResult()) {
                            WaybillLog waybillLog = new WaybillLog();
                            waybillLog.setWaybill(waybill);
                            waybillLog.setPreviousStatus(waybill.getStatus()); // Status 1: Đang đợi lấy hàng
                            waybillLog.setCurrentStatus(4);
                            waybillLogRepository.save(waybillLog);

                            waybill.setStatus(4); // Status 4 là trạng thái Hủy
                            waybillRepository.save(waybill);
                        }
                    }
                }
            }*/
        } else {
            throw new RuntimeException(String
                    .format("Order with code %s is in delivery or has been cancelled. Please check again!", code));
        }
    }


    @Override
    public ConfirmedOrderResponse createClientOrder(OrderRequest request) {
        Map<String, Object> customerData = fetchCustomerData(request.getCustomerId());

        var orderEntity = mapToOrderEntity(request, customerData);
        List<PurchaseResponse> purchaseResponses = processProductPurchases(request.getPurchaseProducts());

        orderEntity.setOrderLines(mapToOrderLineEntities(orderEntity, purchaseResponses));
        setOrderTotals(orderEntity, request.getShippingCost(), request.getPaymentMethod());

        orderRepository.save(orderEntity);

        ConfirmedOrderResponse confirmedOrderResponse = createPaymentAndGetResponse(orderEntity, request.getPaymentMethod());

        if (request.getCustomerId() != null) {
            cleanCart(request);
        }

        return confirmedOrderResponse;
    }

    private Map<String, Object> fetchCustomerData(String customerId) {
        if (customerId == null) return null;

        var customerFetch = clientProxy.findUserByUserId(customerId)
                .orElseThrow(() -> new BusinessException("Cannot create order :: No customer found with ID: " + customerId));

        return (Map<String, Object>) customerFetch.data().get("user");
    }

    // Helper method to process product purchases and return responses
    private List<PurchaseResponse> processProductPurchases(List<PurchaseRequest> purchaseProducts) {
        var purchaseProductsResponse = productClientProxy.purchaseProducts(purchaseProducts);

        if (purchaseProductsResponse.getStatus() != 200) {
            throw new BusinessException("Cannot create order :: Error purchasing products", purchaseProductsResponse.getData());
        }
        return purchaseProductsResponse.getData();
    }


    // Helper method to set order totals and advance payment
    private void setOrderTotals(OrderEntity orderEntity, BigDecimal shippingCost, PaymentMethod paymentMethod) {
        BigDecimal totalAmount = calculateTotalAmount(orderEntity.getOrderLines());
        BigDecimal totalPay = calculateTotalPay(totalAmount, shippingCost);

        orderEntity.setTotalAmount(totalAmount);
        orderEntity.setTax(totalAmount.multiply(BigDecimal.valueOf(0.1)));
        orderEntity.setTotalPay(totalPay);

        BigDecimal advancePayment = (paymentMethod == PaymentMethod.CASH)
                ? totalPay.multiply(BigDecimal.valueOf(0.25))
                : totalPay;

        orderEntity.setAdvancePayment(advancePayment);
    }

    // Helper method to create PayPal payment and set response details
    private ConfirmedOrderResponse createPaymentAndGetResponse(OrderEntity orderEntity, PaymentMethod paymentMethod) {
        ConfirmedOrderResponse response = new ConfirmedOrderResponse();
        response.setOrderCode(orderEntity.getCode());
        response.setPaymentMethod(paymentMethod);

        BigDecimal advancePayment = orderEntity.getAdvancePayment();

        try {
            Payment payment = paypalConfig.createPayment(
                    advancePayment.doubleValue(),
                    "USD",
                    "paypal",
                    "sale",
                    "Order payment",
                    HOST_URL + SERVICE_NAME + "/api/v1/orders/payment/cancel",
                    HOST_URL + SERVICE_NAME + "/api/v1/orders/payment/success"
            );

            if (payment != null && "created".equals(payment.getState())) {
                orderEntity.setPaypalOrderId(payment.getId());
                orderEntity.setPaypalOrderStatus(payment.getState());
                orderRepository.save(orderEntity);

                payment.getLinks().stream()
                        .filter(link -> "approval_url".equals(link.getRel()))
                        .findFirst()
                        .ifPresent(link -> response.setOrderPaypalCheckoutLink(link.getHref()));
            }
        } catch (Exception e) {
            log.error("Error creating Paypal transaction: {}", e.getMessage());
            throw new BusinessException("Error Create Paypal Transaction Request :: " + e.getMessage());
        }
        return response;
    }


    private void cleanCart(OrderRequest request) {
        var cartEntityOptional = cartRepository.findByCustomerId(request.getCustomerId());

        if (cartEntityOptional.isPresent()) {
            CartEntity cartEntity = cartEntityOptional.get();
            CartVariantKeyRequest cartVariantKeyRequest = new CartVariantKeyRequest();

            cartVariantKeyRequest.setCartId(cartEntity.getCartId());

            Map<String, List<String>> itemDeleteRequests = request.getPurchaseProducts().stream()
                    .collect(Collectors.groupingBy(PurchaseRequest::variantId,
                            Collectors.flatMapping(purchaseRequest -> Stream.of(
                                    purchaseRequest.productId(),
                                    purchaseRequest.paintId(),
                                    purchaseRequest.floorId(),
                                    purchaseRequest.wallpaperId()
                            ).filter(Objects::nonNull), Collectors.toList())));

            cartVariantKeyRequest.setItemDeleteRequests(itemDeleteRequests);

            cartService.deleteCartItem(cartVariantKeyRequest);
        }
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
        var order = orderRepository.findByPaypalOrderId(paypalOrderId).get();

        try {
            // (1) Capture
            paypalHttpClient.capturePaypalTransaction(paypalOrderId, payerId);

            // (2) Cập nhật order
            if (order.getPaymentMethod() == PaymentMethod.CASH) {
                order.setPaymentStatus(3); // Status 3: Đã thanh toán đặt cọc (25%)
            } else {
                order.setPaymentStatus(2); // Status 2: Đã thanh toán
            }
            order.setPaypalOrderStatus(OrderStatus.COMPLETED.toString());

            // (3) Gửi notification
            sendEmail(order, EventType.ORDER_CREATED);
            // (4) Lưu order
            orderRepository.save(order);
        } catch (Exception e) {
            log.error("Cannot capture transaction: {0}", e);
        }

    }

    public void sendEmail(OrderEntity order, EventType eventType) {
        var customer = this.clientProxy.findUserByUserId(order.getCustomerId())
                .orElseThrow(() -> new BusinessException("Cannot create cart :: No customer found with ID: " + order.getCustomerId()));

        List<CartVariantRequest> cartVariantRequests = order.getOrderLines().stream()
                .map(orderLine -> {
                    CartVariantRequest request = new CartVariantRequest();
                    request.setVariantId(orderLine.getVariantId());
                    request.setProductId(orderLine.getProductId());
                    request.setPaintId(orderLine.getPaintId());
                    request.setWallpaperId(orderLine.getWallpaperId());
                    request.setFloorId(orderLine.getFloorId());
                    request.setQuantity(orderLine.getQuantity());
                    return request;
                })
                .toList();

        var variantResponses = this.productClientProxy.getProductByVariantId(cartVariantRequests);

        if (customer != null) {
            Map<String, Object> data = (Map<String, Object>) customer.data().get("user");
            publisher.publishEvent(new OrderEvent(order, eventType, variantResponses, data));
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

    @Override
    public GhnCalculateFeeResponse calculateFee(GhnCalculateFeeRequest ghnCalculateFeeRequestRequest) {
        String calculateFeePath = ghnApiPath + "/v2/shipping-order/fee";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Token", ghnToken);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<GhnCalculateFeeRequest> request = new HttpEntity<>(ghnCalculateFeeRequestRequest, headers);
        ResponseEntity<GhnCalculateFeeResponse> response = restTemplate.postForEntity(calculateFeePath, request, GhnCalculateFeeResponse.class);

        return response.getBody();
    }

    @Override
    public Map getProvinces() {
        String GHNProvincePath = ghnApiPath + "/master-data/province";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Token", ghnToken);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(GHNProvincePath, HttpMethod.GET, request, Map.class);

        return response.getBody();
    }

    @Override
    public Map<String, Object> getDistrict(JsonNode districtId) {
        String GHNDistrictPath = ghnApiPath + "/master-data/district";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Token", ghnToken);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<JsonNode> request = new HttpEntity<>(districtId, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(GHNDistrictPath, request, Map.class);

        return response.getBody();
    }

    @Override
    public Map<String, Object> getWard(JsonNode wardId) {
        String GHNWardPath = ghnApiPath + "/master-data/ward?district_id";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("Token", ghnToken);

        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<JsonNode> request = new HttpEntity<>(wardId, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(GHNWardPath, request, Map.class);

        return response.getBody();
    }


}
