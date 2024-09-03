package com.dcode.order_service.service.impl;

import com.dcode.order_service.dto.order.request.OrderLineRequest;
import com.dcode.order_service.dto.order.response.OrderLineResponse;
import com.dcode.order_service.repository.IOrderLineRepository;
import com.dcode.order_service.service.IOrderLineService;
import com.dcode.order_service.utils.OrderUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.dcode.order_service.utils.OrderUtils.createNewOrderLineEntity;
import static com.dcode.order_service.utils.OrderUtils.fromOrderLineEntity;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderLineServiceImpl implements IOrderLineService {

    private final IOrderLineRepository repository;

    public List<OrderLineResponse> findAllByOrderId(String orderId) {
        return repository.findAllByOrderId(orderId)
                .stream()
                .map(OrderUtils::fromOrderLineEntity)
                .collect(Collectors.toList());
    }

    public Long saveOrderLine(OrderLineRequest request) {
        var order = createNewOrderLineEntity(request);
        return repository.save(order).getId();
    }


}
