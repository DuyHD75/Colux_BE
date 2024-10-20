package com.dcode.order_service.service.impl;

import com.dcode.order_service.domain.kafka.OrderConfirmation;
//import com.dcode.order_service.domain.kafka.OrderProducer;
import com.dcode.order_service.dto.order.Order;
import com.dcode.order_service.dto.order.request.OrderLineRequest;
import com.dcode.order_service.dto.order.request.OrderRequest;
import com.dcode.order_service.dto.order.response.OrderResponse;
import com.dcode.order_service.dto.payment.PaypalRequest;
import com.dcode.order_service.dto.product.PurchaseRequest;
import com.dcode.order_service.enumuration.PaymentMethod;
import com.dcode.order_service.event.listener.OrderEvent;
import com.dcode.order_service.exception.BusinessException;

import com.dcode.order_service.proxy.ICustomerClientProxy;
import com.dcode.order_service.proxy.ProductClientProxy;
import com.dcode.order_service.repository.IOrderRepository;
import com.dcode.order_service.service.IOrderLineService;
import com.dcode.order_service.service.IOrderService;
import com.dcode.order_service.utils.OrderUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.dcode.order_service.constant.Constants.AppConstants.HOST_URL;
import static com.dcode.order_service.enumuration.EventType.ORDER_CREATED;
import static com.dcode.order_service.enumuration.TransactionIntent.CAPTURE;
import static com.dcode.order_service.enumuration.PaymentPage.BILLING;
import static com.dcode.order_service.utils.OrderUtils.createNewOrderEntity;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements IOrderService {

    private final ICustomerClientProxy clientProxy;
    private final ProductClientProxy productClientProxy;

    private final IOrderRepository orderRepository;

    private final IOrderLineService orderLineService;
    private final ApplicationEventPublisher publisher;

    private static final int VND_TO_USD = 23_000;

//    private final OrderProducer orderProducer;

    @Override
    public void cancelOrder(String code) {
//     This method is not implemented yet
    }

    @Override
    public void createClientOrder(OrderRequest request) {
        var customer = this.clientProxy.findUserByUserId(request.getCustomerId())
                .orElseThrow(() -> new BusinessException("Cannot create order :: No customer found with ID: " + request.getCustomerId()));

        log.info("Customer found: {}", customer);


        var purchasedProducts = this.productClientProxy.purchaseProducts(request.getPurchaseProducts());

        var order = this.orderRepository.save(createNewOrderEntity(request));

        for (PurchaseRequest purchaseRequest : request.getPurchaseProducts()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            order.getOrderId(),
                            purchaseRequest.productId(),
                            purchaseRequest.variantId(),
                            purchaseRequest.colorId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        if (request.getPaymentMethod() == PaymentMethod.CASH) {
            orderRepository.save(order);
        } else if (request.getPaymentMethod() == PaymentMethod.PAYPAL) {
            try {

                BigDecimal convertTotalPayUSD = request.getTotalPay()
                        .divide(BigDecimal.valueOf(VND_TO_USD), 0, BigDecimal.ROUND_HALF_UP);

                PaypalRequest paypalRequest = new PaypalRequest();

                paypalRequest.setIntent(CAPTURE);
                paypalRequest.setPurchaseUnits(
                        List.of(
                                new PaypalRequest.PurchaseUnit(
                                        new PaypalRequest.PurchaseUnit.Money("USD", convertTotalPayUSD.toString())
                                )
                        )
                );
                paypalRequest.setApplicationContext(
                        new PaypalRequest.PayPalAppContext()
                                .setBrandName("Colux")
                                .setLandingPage(BILLING)
                                .setReturnUrl(HOST_URL + "/api/v1/orders/paypal/capture")
                                .setCancelUrl(HOST_URL + "/api/v1/orders/paypal/cancel")
                );

            } catch (Exception e) {
                log.error("Error while processing payment", e);
                throw new BusinessException("Error while processing payment");
            }
        }

        // send kafka event
       /* orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.getReference(),
                        request.getTotalPay(),
                        request.getPaymentMethod(),
                        customer,
                        purchasedProducts
                )
        );*/
        // send kafka event
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

/*
    public PurchaseResponseWrapper purchaseProducts(List<PurchaseRequest> purchaseRequests) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        HttpEntity<List<PurchaseRequest>> requestEntity = new HttpEntity<>(purchaseRequests, headers);

        try {
            ResponseEntity<PurchaseResponseWrapper> responseEntity = restTemplate.exchange(
                    PRODUCT_URL + "/purchase-order",
                    POST,
                    requestEntity,
                    PurchaseResponseWrapper.class
            );

            PurchaseResponseWrapper responseWrapper = responseEntity.getBody();
            assert responseWrapper != null;
            responseWrapper.setStatus(responseEntity.getStatusCode().value());
            return responseWrapper;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            try {
                PurchaseResponseWrapper errorResponse = new ObjectMapper().readValue(e.getResponseBodyAsString(), PurchaseResponseWrapper.class);
                errorResponse.setStatus(e.getStatusCode().value());
                return errorResponse;
            } catch (JsonProcessingException jsonException) {
                throw new BusinessException("Error while parsing error response from product service");
            }
        } catch (Exception e) {
            log.error("here: ", e);
            throw new BusinessException("Unexpected error while purchasing products");
        }
    }*/


}
