package com.dcode.order_service.service;

import com.dcode.order_service.dto.shipment.ShipmentDto;

import java.util.List;

public interface IShipmentService {
    ShipmentDto createOrUpdateShipment(ShipmentDto shipmentDto);

    void deleteShipment(String customerId, String shipmentId);

    List<ShipmentDto> getShipment(String customerId);
}
