package com.dcode.order_service.service.impl;

import com.dcode.order_service.dto.order.request.GhnCallbackOrderRequest;
import com.dcode.order_service.dto.order.request.GhnCreateOrderRequest;
import com.dcode.order_service.dto.order.request.WaybillRequest;
import com.dcode.order_service.dto.order.response.GhnCreateOrderResponse;
import com.dcode.order_service.dto.order.response.WaybillResponse;
import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.entity.order.OrderLineEntity;
import com.dcode.order_service.entity.waybill.Waybill;
import com.dcode.order_service.entity.waybill.WaybillLog;
import com.dcode.order_service.enumuration.PaymentMethod;
import com.dcode.order_service.enumuration.WaybillCallbackConstants;
import com.dcode.order_service.exception.BusinessException;
import com.dcode.order_service.repository.IOrderRepository;
import com.dcode.order_service.repository.IWaybillLogRepository;
import com.dcode.order_service.repository.IWaybillRepository;
import com.dcode.order_service.service.WaybillService;
import com.dcode.order_service.utils.WaybillUtils;
import lombok.RequiredArgsConstructor;
//import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
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

    @Override
    public void callbackStatusWaybillFromGHN(GhnCallbackOrderRequest ghnCallbackOrderRequest) {
        if (Objects.equals(ghnCallbackOrderRequest.getShopID().toString(), ghnShopId)) {
            Waybill waybill = waybillRepository.findByCode(ghnCallbackOrderRequest.getOrderCode())
                    .orElseThrow(() -> new BusinessException("Waybill not found!"));

            OrderEntity order = waybill.getOrder();

            WaybillLog waybillLog = new WaybillLog();
            waybillLog.setWaybill(waybill);
            waybillLog.setPreviousStatus(waybill.getStatus());

            int currentWaybillStatus = WaybillCallbackConstants.WAYBILL_STATUS_CODE
                    .get(ghnCallbackOrderRequest.getStatus());

            if (!waybill.getStatus().equals(currentWaybillStatus)) {
                switch (currentWaybillStatus) {
                    case WaybillCallbackConstants.WAITING:
                        waybillLog.setCurrentStatus(1);
                        waybill.setStatus(1);
                        order.setStatus(2);
                        break;
                    case WaybillCallbackConstants.SHIPPING:
                        // sử dụng mail hoặc notification để thông báo cho người dùng
                        /*createNotification(new Notification()
                                .setUser(order.getUser())
                                .setType(NotificationType.ORDER)
                                .setMessage(String.format("Đơn hàng %s của bạn đang được vận chuyển.", order.getCode()))
                                .setAnchor("/order/detail/" + order.getCode())
                                .setStatus(1));*/
                        waybillLog.setCurrentStatus(2);
                        waybill.setStatus(2);
                        order.setStatus(3);
                        break;
                    case WaybillCallbackConstants.SUCCESS:
                        // sử dụng mail hoặc notification để thông báo cho người dùng
                        /*createNotification(new Notification()
                                .setUser(order.getUser())
                                .setType(NotificationType.ORDER)
                                .setMessage(String.format("Đơn hàng %s của bạn đã giao thành công!", order.getCode()))
                                .setAnchor("/order/detail/" + order.getCode())
                                .setStatus(1));*/
                        // TODO: KHI HOÀN THÀNH ĐƠN HÀNG CẦN GỬI MAIL CHO KHÁCH HÀNG
                        waybillLog.setCurrentStatus(3);
                        waybill.setStatus(3);
                        order.setStatus(4);
                        // Status 2: Đã thanh toán (giả định giao thành công thì
                        // cũng có nghĩa khách hàng đã thanh toán tiền mặt)
                        order.setPaymentStatus(2);

                        // Tích điểm
//                        rewardUtils.successOrderHook(order);
                        break;
                    case WaybillCallbackConstants.FAILED:

                    case WaybillCallbackConstants.RETURN:
                        // TODO: CẦN THỐNG NHẤT VỀ CÁCH TRẢ HÀNG HOẶC HỦY ĐƠN HÀNG
                        /*createNotification(new Notification()
                                .setUser(order.getUser())
                                .setType(NotificationType.ORDER)
                                .setMessage(String.format("Đơn hàng %s của bạn đã bị hủy.", order.getCode()))
                                .setAnchor("/order/detail/" + order.getCode())
                                .setStatus(1));*/
                        waybillLog.setCurrentStatus(4);
                        waybill.setStatus(4);// 5
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
            throw new RuntimeException("ShopId is not valid");
        }
    }

    @Override
    public WaybillResponse createAWaybill(WaybillRequest waybillRequest) {
        var waybillOpt = waybillRepository.findByOrder_OrderId(waybillRequest.getOrderId());
        if (waybillOpt.isPresent()){
            throw new BusinessException("This order already had a waybill. Please choose another order!");
        }

        OrderEntity order = orderRepository.findByOrderId(waybillRequest.getOrderId())
                .orElseThrow(() -> new BusinessException("Order not found!"));

        // tạo waybill khi order.status = 1
        if (order.getStatus() == 1) {
            String createGhnOrderApiPath = ghnApiPath + "/shipping-order/create";

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
        for (OrderLineEntity orderLineEntity : order.getOrderLines()) {
            var item = new GhnCreateOrderRequest.Item();
            item.setName(buildGhnProductName(orderLineEntity.getProductId(),
                    orderLineEntity.getVariantId()));
            item.setQuantity(orderLineEntity.getQuantity());
            item.setPrice(orderLineEntity.getTrackingPrice().intValue());
            items.add(item);
        }
        ghnCreateOrderRequest.setItems(items);

        return ghnCreateOrderRequest;
    }
    private int chooseGhnPaymentTypeId(PaymentMethod paymentMethodType) {
        return paymentMethodType == PaymentMethod.CASH
                ? 2 // Thanh toán tiền mặt, người nhận trả tiền vận chuyển và tiền thu hộ
                : 1; // Thanh toán PayPal, Người gửi trả tiền vận chuyển
    }
    @SuppressWarnings("unchecked")
    private String buildGhnProductName(String productName, @Nullable String variantProperties) {
//        ObjectMapper mapper = new ObjectMapper();
//
//        CollectionWrapper<LinkedHashMap<String, Object>> variantPropertiesObj;
//
//        try {
//            variantPropertiesObj = mapper.readValue(variantProperties, CollectionWrapper.class);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException("Cannot build product name for GHN order");
//        }
//
//        if (variantPropertiesObj == null) {
//            return productName;
//        }
//
//        StringJoiner joiner = new StringJoiner(", ", "(", ")");
//
//        for (var variantProperty : variantPropertiesObj.getContent()) {
//            joiner.add(String.format("%s: %s", variantProperty.get("name"), variantProperty.get("value")));
//        }
//
//        return String.format("%s %s", productName, joiner);
        return productName;
    }


}
