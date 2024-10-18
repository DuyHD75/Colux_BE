package com.dcode.order_service.service.impl;

import com.dcode.order_service.config.PaypalConfig;
import com.dcode.order_service.domain.kafka.OrderConfirmation;
//import com.dcode.order_service.domain.kafka.OrderProducer;
import com.dcode.order_service.dto.order.Order;
import com.dcode.order_service.dto.order.request.OrderLineRequest;
import com.dcode.order_service.dto.order.request.OrderRequest;
import com.dcode.order_service.dto.order.response.OrderResponse;
import com.dcode.order_service.dto.payment.PaypalRequest;
import com.dcode.order_service.dto.payment.PaypalResponse;
import com.dcode.order_service.dto.product.PurchaseRequest;
import com.dcode.order_service.dto.product.PurchaseResponse;
import com.dcode.order_service.enumuration.PaymentMethod;
import com.dcode.order_service.event.listener.OrderEvent;
import com.dcode.order_service.exception.BusinessException;

import com.dcode.order_service.proxy.ICustomerClientProxy;
import com.dcode.order_service.proxy.ProductClientProxy;
import com.dcode.order_service.repository.IOrderRepository;
import com.dcode.order_service.service.IOrderLineService;
import com.dcode.order_service.service.IOrderService;
import com.dcode.order_service.utils.OrderUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private final PaypalConfig paypalConfig;

    private static final int VND_TO_USD = 23_000;

//    private final OrderProducer orderProducer;

    @Override
    public void cancelOrder(String code) {
//     This method is not implemented yet
    }

    @Override
    public void createClientOrder(OrderRequest request) {
        var customer = this.clientProxy.findUserByUserId(request.getCustomerId())
                .orElseThrow(() -> new BusinessException("Cannot create order :: No customer found with ID: " + request.getToWardName()));

        log.info("Customer found: {}", customer);
        log.info("Sending purchase order request: {}", request.getPurchaseProducts());
        List<PurchaseResponse> purchasedProducts = productClientProxy.purchaseProducts(request.getPurchaseProducts());

        // tạo map để ánh xạ giữa request và response để tạo orderLine phía bên dưới
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

        for (PurchaseResponse response : purchasedProducts) {
            if (!response.getSuccess()) {
                log.warn("Product purchase failed: {}", response.getMessage());
                // Xử lý các phản hồi từ purchasedProducts
            }
        }

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
            }else {
                log.warn("No corresponding response found for productId: {}", purchaseRequest.productId());
            }
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

              /*  // Gửi yêu cầu thanh toán đến PayPal
                PaypalResponse paypalResponse = paypalConfig.createPayment(paypalRequest);

                // Xử lý phản hồi từ PayPal
                if (paypalResponse != null && paypalResponse.getStatus().equals("CREATED")) {
                    // Lưu thông tin giao dịch PayPal vào cơ sở dữ liệu nếu cần
                    // Chuyển hướng người dùng đến URL để hoàn tất thanh toán
                    String approvalUrl = paypalResponse.getLinks().stream()
                            .filter(link -> link.getRel().equals("approve"))
                            .findFirst()
                            .orElseThrow(() -> new BusinessException("No approval URL found in PayPal response"))
                            .getHref();

                    // Chuyển hướng người dùng đến approvalUrl để hoàn tất thanh toán
                    // Bạn có thể trả về URL này trong phản hồi API hoặc chuyển hướng trực tiếp nếu là ứng dụng web
                    log.info("Redirecting user to PayPal for payment approval: {}", approvalUrl);
                } else {
                    throw new BusinessException("Error while creating PayPal payment");
                }*/

            } catch (Exception e) {
                log.error("Error while processing payment", e);
                throw new BusinessException("Error while processing payment");
            }
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


}
