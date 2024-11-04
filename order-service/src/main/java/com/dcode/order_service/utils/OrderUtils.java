package com.dcode.order_service.utils;

import com.dcode.order_service.dto.order.Order;
import com.dcode.order_service.dto.order.request.OrderLineRequest;
import com.dcode.order_service.dto.order.request.OrderRequest;
import com.dcode.order_service.dto.order.response.OrderLineResponse;
import com.dcode.order_service.dto.product.PurchaseResponse;
import com.dcode.order_service.entity.order.OrderLineEntity;
import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.repository.IOrderLineRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dcode.order_service.constant.Constants.AppConstants.DEFAULT_TAX;


@Service
public class OrderUtils {

    public static OrderEntity mapToOrderEntity(OrderRequest request, Map<? , ?> data) {
        return OrderEntity.builder()
                .orderId(UUID.randomUUID().toString())
                .customerId(data != null ? data.get("userId").toString() : UUID.randomUUID().toString())
                .code(RandomString.make(12).toUpperCase())
                .status(1) // Status 1: Đơn hàng mới
                .toName(request.getToName())
                .toPhone(request.getToPhone())
                .toEmail(request.getToEmail())
                .toAddress(request.getToAddress())
                .toWardName(request.getToWardName())
                .toDistrictName(request.getToDistrictName())
                .toProvinceName(request.getToProvinceName())
                .note(request.getNote())
                .shippingCost(request.getShippingCost())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(request.getPaymentStatus())
                .build();
    }

    public static Set<OrderLineEntity> mapToOrderLineEntities(OrderEntity orderEntity, List<PurchaseResponse> purchaseResponses) {
        return purchaseResponses.stream()
                .map(response -> {
                    var orderLine = new OrderLineEntity();
                    orderLine.setOrderLineId(UUID.randomUUID().toString());
                    orderLine.setOrderEntity(orderEntity);
                    orderLine.setProductId(response.getProductId());
                    orderLine.setFloorId(response.getFloorId());
                    orderLine.setPaintId(response.getPaintId());
                    orderLine.setWallpaperId(response.getWallpaperId());
                    orderLine.setVariantId(response.getVariantId());
                    orderLine.setQuantity(response.getQuantity());
                    orderLine.setTrackingPrice(response.getPrice());
                    orderLine.setAmount(response.getPrice().multiply(BigDecimal.valueOf(response.getQuantity())));
                    return orderLine;
                })
                .collect(Collectors.toSet());
    }

    public static BigDecimal calculateTotalAmount(Set<OrderLineEntity> orderLines) {
        return BigDecimal.valueOf(orderLines.stream()
                .mapToDouble(line -> line.getAmount().doubleValue())
                .sum());
    }

    public static BigDecimal calculateTotalPay(BigDecimal totalAmount, BigDecimal shippingCost) {
        return totalAmount
                .add(totalAmount.multiply(BigDecimal.valueOf(DEFAULT_TAX)).setScale(0, RoundingMode.HALF_UP))
                .add(shippingCost);
    }

    public static Order fromOrderEntity(OrderEntity orderEntity) {
        Order order = new Order();
        BeanUtils.copyProperties(orderEntity, order);
        order.setCode(orderEntity.getCode());
        return null;
    }


    public static OrderLineEntity toOrderLineEntity(OrderLineRequest request) {
        return OrderLineEntity.builder()
                .orderLineId(UUID.randomUUID().toString())
                .productId(request.productId())
                .quantity(request.quantity())
                .variantId(request.variantId())
                .paintId(request.paintId())
                .wallpaperId(request.wallpaperId())
                .floorId(request.floorId())
                .build();
    }

    public static OrderLineResponse fromOrderLineEntity(OrderLineEntity orderLine) {
        return new OrderLineResponse(
                orderLine.getId(),
                orderLine.getProductId(),
                orderLine.getVariantId(),
                orderLine.getPaintId(),
                orderLine.getWallpaperId(),
                orderLine.getFloorId(),
                orderLine.getQuantity()
        );
    }


}
