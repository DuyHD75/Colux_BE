package com.dcode.order_service.utils;

import com.dcode.order_service.dto.shipment.ShipmentDto;
import com.dcode.order_service.entity.order.ShipmentEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class ShipmentUtils {

    public ShipmentEntity creatNewShipment(ShipmentDto shipmentDto) {
        return ShipmentEntity.builder()
                .customerName(shipmentDto.getCustomerName())
                .customerPhone(shipmentDto.getCustomerPhone())
                .shipmentId(UUID.randomUUID().toString())
                .customerId(shipmentDto.getCustomerId())
                .toAddress(shipmentDto.getToAddress())
                .toWardName(shipmentDto.getToWardName())
                .toDistrictName(shipmentDto.getToDistrictName())
                .toProvinceName(shipmentDto.getToProvinceName())
                .status(shipmentDto.getStatus())
                .build();
    }

    public ShipmentEntity partialUpdate(ShipmentEntity existingEntity, ShipmentDto shipmentDto) {
        existingEntity.setCustomerId(shipmentDto.getCustomerId());
        existingEntity.setCustomerName(shipmentDto.getCustomerName());
        existingEntity.setCustomerPhone(shipmentDto.getCustomerPhone());
        existingEntity.setToAddress(shipmentDto.getToAddress());
        existingEntity.setToWardName(shipmentDto.getToWardName());
        existingEntity.setToDistrictName(shipmentDto.getToDistrictName());
        existingEntity.setToProvinceName(shipmentDto.getToProvinceName());
        existingEntity.setStatus(shipmentDto.getStatus());
        return existingEntity;
    }

    public ShipmentDto entityToDto(ShipmentEntity shipmentEntity) {
        ShipmentDto shipmentDto = new ShipmentDto();
        shipmentDto.setCustomerId(shipmentEntity.getCustomerId());
        shipmentDto.setCustomerName(shipmentEntity.getCustomerName());
        shipmentDto.setCustomerPhone(shipmentEntity.getCustomerPhone());
        shipmentDto.setShipmentId(shipmentEntity.getShipmentId());
        shipmentDto.setToAddress(shipmentEntity.getToAddress());
        shipmentDto.setToWardName(shipmentEntity.getToWardName());
        shipmentDto.setToDistrictName(shipmentEntity.getToDistrictName());
        shipmentDto.setToProvinceName(shipmentEntity.getToProvinceName());
        shipmentDto.setStatus(shipmentEntity.getStatus());
        return shipmentDto;
    }
}
