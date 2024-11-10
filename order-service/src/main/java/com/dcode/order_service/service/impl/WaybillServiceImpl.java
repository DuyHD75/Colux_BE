package com.dcode.order_service.service.impl;

import com.dcode.order_service.domain.Response;
import com.dcode.order_service.dto.cart.request.CartVariantRequest;
import com.dcode.order_service.dto.order.request.GhnCallbackOrderRequest;
import com.dcode.order_service.dto.order.request.GhnCreateOrderRequest;
import com.dcode.order_service.dto.order.request.WaybillRequest;
import com.dcode.order_service.dto.order.response.GhnCreateOrderResponse;
import com.dcode.order_service.dto.order.response.WaybillResponse;
import com.dcode.order_service.dto.waybill.request.GhnDetailOrderRequest;
import com.dcode.order_service.dto.waybill.response.GhnDetailOrderResponse;
import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.entity.order.OrderLineEntity;
import com.dcode.order_service.entity.waybill.Waybill;
import com.dcode.order_service.entity.waybill.WaybillLog;
import com.dcode.order_service.enumuration.PaymentMethod;
import com.dcode.order_service.enumuration.WaybillCallbackConstants;
import com.dcode.order_service.exception.BusinessException;
import com.dcode.order_service.proxy.ICustomerClientProxy;
import com.dcode.order_service.proxy.IProductClientProxy;
import com.dcode.order_service.repository.IOrderRepository;
import com.dcode.order_service.repository.IWaybillLogRepository;
import com.dcode.order_service.repository.IWaybillRepository;
import com.dcode.order_service.service.WaybillService;
import com.dcode.order_service.utils.WaybillUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WaybillServiceImpl implements WaybillService {

    @Value("${spring.shipping.ghnToken}")
    private String ghnToken;
    @Value("${spring.shipping.ghnShopId}")
    private String ghnShopId;
    @Value("${spring.shipping.ghnApiPath}")
    private String ghnApiPath;

    private final IWaybillRepository waybillRepository;
    private final IOrderRepository orderRepository;
    private final IWaybillLogRepository waybillLogRepository;
    private final IProductClientProxy IProductClientProxy;

    @Autowired
    private ObjectMapper objectMapper;

    @Scheduled(fixedRate = 300000)
    public void checkOrderShipmentStatus() {
        List<Waybill> waybills = waybillRepository.findAllByStatusIn(List.of(1, 2));
        if (!waybills.isEmpty()) {
            String checkGhnOrderApiPath = ghnApiPath + "/v2/shipping-order/detail";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.add("Token", ghnToken);

            RestTemplate restTemplate = new RestTemplate();

            waybills.forEach(waybill -> {
                var request = new HttpEntity<>(new GhnDetailOrderRequest(waybill.getCode()), headers);
                var response = restTemplate.postForEntity(checkGhnOrderApiPath, request, GhnDetailOrderResponse.class);

                if (response.getStatusCode() != HttpStatus.OK) {
                    throw new RuntimeException("Error when calling status Order GHN API");
                }

                if (response.getBody() != null) {
                    var ghnCreateOrderResponse = response.getBody();

                    OrderEntity order = waybill.getOrder();

                    WaybillLog waybillLog = new WaybillLog();
                    waybillLog.setWaybill(waybill);
                    waybillLog.setPreviousStatus(waybill.getStatus());

                    int currentWaybillStatus = WaybillCallbackConstants.WAYBILL_STATUS_CODE
                            .get(ghnCreateOrderResponse.getData().getStatus());

                    if (!waybill.getStatus().equals(currentWaybillStatus)) {
                        switch (currentWaybillStatus) {
                            case WaybillCallbackConstants.WAITING:
                                waybillLog.setCurrentStatus(1);
                                waybill.setStatus(1);
                                order.setStatus(2);
                                break;
                            case WaybillCallbackConstants.SHIPPING:
                                waybillLog.setCurrentStatus(2);
                                waybill.setStatus(2);
                                order.setStatus(3);
                                break;
                            case WaybillCallbackConstants.SUCCESS:
                                // Gửi mail khi success
                                waybillLog.setCurrentStatus(3);
                                waybill.setStatus(3);
                                order.setStatus(4);
                                // Status 2: Đã thanh toán (giả định giao thành công thì
                                // cũng có nghĩa khách hàng đã thanh toán tiền mặt)
                                order.setPaymentStatus(2);
                                break;
                            case WaybillCallbackConstants.FAILED:
                                waybillLog.setCurrentStatus(4);
                                waybill.setStatus(4);
                                order.setStatus(5);
                            case WaybillCallbackConstants.RETURN:
                                // TODO: gửi mail
                                waybillLog.setCurrentStatus(4);
                                waybill.setStatus(4);
                                order.setStatus(5);
                                break;
                            default:
                                throw new RuntimeException("There is no waybill status corresponding to GHN status code");
                        }

                        waybillRepository.save(waybill);
                        orderRepository.save(order);
                        waybillLogRepository.save(waybillLog);
                    }

                } else {
                    throw new RuntimeException("Response from Check Order GHN API cannot use");
                }
            });
        }
    }

    @Override
    public WaybillResponse createAWaybill(WaybillRequest waybillRequest) {
        var waybillOpt = waybillRepository.findByOrder_OrderId(waybillRequest.getOrderId());
        if (waybillOpt.isPresent()) {
            throw new BusinessException("This order already had a waybill. Please choose another order!");
        }

        OrderEntity order = orderRepository.findByOrderId(waybillRequest.getOrderId())
                .orElseThrow(() -> new BusinessException("Order not found!"));

        // tạo waybill khi order.status = 1
        if (order.getStatus() == 1) {
            String createGhnOrderApiPath = ghnApiPath + "/v2/shipping-order/create";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.add("Token", ghnToken);
            headers.add("ShopId", ghnShopId);

            RestTemplate restTemplate = new RestTemplate();

            var request = new HttpEntity<>(buildGhnCreateOrderRequest(waybillRequest, order), headers);
            var response = restTemplate.postForEntity(createGhnOrderApiPath, request, GhnCreateOrderResponse.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Error when calling Create Order GHN API");
            }

            if (response.getBody() != null) {
                var ghnCreateOrderResponse = response.getBody();

                // (1) Tạo waybill
                Waybill waybill = WaybillUtils.requestToEntity(waybillRequest);

                waybill.setCode(ghnCreateOrderResponse.getData().getOrderCode());
                waybill.setOrder(order);
                waybill.setExpectedDeliveryTime(ghnCreateOrderResponse.getData().getExpectedDeliveryTime());
                waybill.setStatus(1); // Status 1: Đang đợi lấy hàng
                waybill.setCodAmount(
                        order.getPaymentMethod() == PaymentMethod.CASH
                                ? order.getTotalPay().intValue()
                                : 0
                );
                waybill.setShippingFee(ghnCreateOrderResponse.getData().getTotalFee());
                waybill.setGhnPaymentTypeId(chooseGhnPaymentTypeId(order.getPaymentMethod()));

                Waybill waybillAfterSave = waybillRepository.save(waybill);

                // (2) Sửa order
                order.setShippingCost(BigDecimal.valueOf(ghnCreateOrderResponse.getData().getTotalFee()));
                order.setTotalPay(BigDecimal.valueOf(
                        order.getTotalPay().intValue() + ghnCreateOrderResponse.getData().getTotalFee()));
                order.setStatus(2); // Status 2: Đang xử lý

                orderRepository.save(order);

                // (2.1) Thêm waybill log
                WaybillLog waybillLog = new WaybillLog();
                waybillLog.setWaybill(waybillAfterSave);
                waybillLog.setCurrentStatus(1); // Status 1: Đang đợi lấy hàng

                waybillLogRepository.save(waybillLog);

                // (3) Thông báo cho người dùng về việc đơn hàng đã được duyệt
                // với thông tin phí vận chuyển và sự thay đổi tổng tiền trả
                // có thể sử dụng email hoặc notification
               /* Notification notification = new Notification()
                        .setUser(order.getUser())
                        .setType(NotificationType.ORDER)
                        .setMessage(
                                order.getPaymentMethodType() == PaymentMethodType.CASH
                                        ? String.format(
                                        "Đơn hàng %s của bạn đã được duyệt. Phí vận chuyển là %s. Tổng tiền cần trả là %s.",
                                        order.getCode(),
                                        NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                                                .format(order.getShippingCost()),
                                        NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                                                .format(order.getTotalPay()))
                                        : String.format("Đơn hàng %s của bạn đã được duyệt.", order.getCode())
                        )
                        .setAnchor("/order/detail/" + order.getCode())
                        .setStatus(1);

                notificationRepository.save(notification);

                notificationService.pushNotification(order.getUser().getUsername(),
                        notificationMapper.entityToResponse(notification));*/

                return WaybillUtils.entityToResponse(waybillAfterSave);
            } else {
                throw new RuntimeException("Response from Create Order GHN API cannot use");
            }


        } else {
            throw new BusinessException("Cannot create a new waybill. Order already had a waybill or was cancelled before.");
        }
    }

    private GhnCreateOrderRequest buildGhnCreateOrderRequest(WaybillRequest waybillRequest, OrderEntity order) {
        GhnCreateOrderRequest ghnCreateOrderRequest = new GhnCreateOrderRequest();

        ghnCreateOrderRequest.setPaymentTypeId(chooseGhnPaymentTypeId(order.getPaymentMethod()));
        ghnCreateOrderRequest.setNote(waybillRequest.getNote());
        ghnCreateOrderRequest.setRequiredNote(waybillRequest.getGhnRequiredNote());
        ghnCreateOrderRequest.setToName(order.getToName());
        ghnCreateOrderRequest.setToPhone(order.getToPhone());
        ghnCreateOrderRequest.setToAddress(order.getToAddress());
        ghnCreateOrderRequest.setToWardName(order.getToWardName());
        ghnCreateOrderRequest.setToDistrictName(order.getToDistrictName());
        ghnCreateOrderRequest.setToProvinceName(order.getToProvinceName());
        ghnCreateOrderRequest.setCodAmount(
                order.getPaymentMethod() == PaymentMethod.CASH
                        ? order.getTotalPay().intValue() // totalPay lúc này là tổng tiền tạm thời
                        : 0
        );
        ghnCreateOrderRequest.setWeight(waybillRequest.getWeight());
        ghnCreateOrderRequest.setLength(waybillRequest.getLength());
        ghnCreateOrderRequest.setWidth(waybillRequest.getWidth());
        ghnCreateOrderRequest.setHeight(waybillRequest.getHeight());
        ghnCreateOrderRequest.setServiceTypeId(2);
        ghnCreateOrderRequest.setServiceId(0);
        ghnCreateOrderRequest.setPickupTime(waybillRequest.getShippingDate().getEpochSecond());

        List<GhnCreateOrderRequest.Item> items = new ArrayList<>();

        List<CartVariantRequest> productOrderRequests = order.getOrderLines().stream()
                .map(orderLine -> new CartVariantRequest(orderLine.getProductId(), orderLine.getVariantId(),
                        orderLine.getPaintId(), orderLine.getWallpaperId(),
                        orderLine.getFloorId()))
                .collect(Collectors.toList());

        Optional<Response> response = IProductClientProxy.findProductInfo(productOrderRequests);
        log.info("Response from product service: {}", response);
        if (response.isPresent() && response.get().data() != null) {
            List<Map<String, Object>> products =
                    objectMapper.convertValue(response.get().data().get("products"), new TypeReference<List<Map<String, Object>>>() {});
            for (OrderLineEntity orderLineEntity : order.getOrderLines()) {
                var item = new GhnCreateOrderRequest.Item();
                Map<String, Object> productInfo = products.stream()
                        .filter(info -> {
                            Object variantResponseObj = info.get("variantResponse");
                            if (variantResponseObj instanceof Map) {
                                Map<String, Object> variantResponse = (Map<String, Object>) variantResponseObj;
                                return variantResponse.get("variantId") != null && variantResponse.get("variantId").equals(orderLineEntity.getVariantId());
                            }
                            return false;
                        })
                        .findFirst()
                        .orElse(null);
                if (productInfo != null) {
                    item.setName(buildGhnProductName(productInfo));
                    item.setQuantity(orderLineEntity.getQuantity());
                    item.setPrice(orderLineEntity.getTrackingPrice());
                    items.add(item);
                }
            }
        }
        ghnCreateOrderRequest.setItems(items);

        return ghnCreateOrderRequest;
    }

    private int chooseGhnPaymentTypeId(PaymentMethod paymentMethodType) {
        return paymentMethodType == PaymentMethod.CASH
                ? 2 // Thanh toán tiền mặt, người nhận trả tiền vận chuyển và tiền thu hộ
                : 1; // Thanh toán PayPal, Người gửi trả tiền vận chuyển
    }

    private String buildGhnProductName(Map<String, Object> productInfo) {
        Map<String, Object> productDetails = objectMapper.convertValue(productInfo.get("productDetails"), new TypeReference<Map<String, Object>>() {
        });
        Map<String, Object> variantResponse = objectMapper.convertValue(productInfo.get("variantResponse"), new TypeReference<Map<String, Object>>() {
        });
        Map<String, Object> paintDetails = productDetails != null ? objectMapper.convertValue(productDetails.get("paintDetails"), new TypeReference<Map<String, Object>>() {
        }) : null;

        String productName = productDetails != null ? (String) productDetails.get("productName") : "";
        String sizeName = variantResponse != null ? (String) variantResponse.get("sizeName") : "";
        String hex = paintDetails != null ? (String) paintDetails.get("hex") : null;

        StringJoiner joiner = new StringJoiner(", ", "(", ")");
        joiner.add("Size: " + sizeName);
        if (hex != null) {
            joiner.add("Color: " + hex);
        }

        return String.format("%s %s", productName, joiner);
    }


}
