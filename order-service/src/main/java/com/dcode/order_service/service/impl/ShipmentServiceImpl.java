package com.dcode.order_service.service.impl;

import com.dcode.order_service.dto.shipment.ShipmentDto;
import com.dcode.order_service.entity.order.ShipmentEntity;
import com.dcode.order_service.repository.IShipmentRepository;
import com.dcode.order_service.service.IShipmentService;
import com.dcode.order_service.utils.ShipmentUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements IShipmentService {

    private final IShipmentRepository shipmentRepository;
    private final ShipmentUtils shipmentUtils;

    @Override
    public ShipmentDto createOrUpdateShipment(ShipmentDto shipmentDto) {

        final ShipmentEntity shipmentBeforeSave;

        if (shipmentDto.getShipmentId() == null) {
            shipmentBeforeSave = shipmentUtils.creatNewShipment(shipmentDto);
        } else {
            shipmentBeforeSave = shipmentRepository.findByShipmentId(shipmentDto.getShipmentId())
                    .map(existingEntity -> shipmentUtils.partialUpdate(existingEntity, shipmentDto))
                    .orElseThrow(() -> new RuntimeException("Cannot create shipment :: No shipment found with ID: " + shipmentDto.getShipmentId()));
        }

        shipmentRepository.save(shipmentBeforeSave);
        return shipmentUtils.entityToDto(shipmentBeforeSave);
    }


    @Transactional
    @Override
    public void deleteShipment(String customerId, String shipmentId) {
        try {
            shipmentRepository.deleteByCustomerIdAndShipmentId(customerId, shipmentId);
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete shipment :: No shipment found with ID: " + shipmentId);
        }
    }

    @Override
    public List<ShipmentDto> getShipment(String customerId) {
        return shipmentRepository.findByCustomerId(customerId)
                .stream()
                .map(shipmentUtils::entityToDto)
                .toList();

    }
}
