package com.dcode.order_service.service.impl;

import com.dcode.order_service.dto.order.request.OrderLineRequest;
import com.dcode.order_service.dto.order.request.OrderRequest;
import com.dcode.order_service.dto.product.PurchaseRequest;
import com.dcode.order_service.exception.BusinessException;
import com.dcode.order_service.proxy.CustomerClientProxy;
import com.dcode.order_service.proxy.ProductClientProxy;
import com.dcode.order_service.repository.IOrderLineRepository;
import com.dcode.order_service.repository.IOrderRepository;
import com.dcode.order_service.service.IOrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.dcode.order_service.utils.OrderUtils.createNewOrderEntity;
import static com.dcode.order_service.utils.OrderUtils.createNewOrderLineEntity;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements IOrderService {

    private final CustomerClientProxy clientProxy;
    private final ProductClientProxy productClientProxy;

    private final IOrderRepository orderRepository;
    private final IOrderLineRepository orderLineRepository;



    @Override
    public void cancelOrder(String code) {
    //
    }

    @Override
    public void createClientOrder(OrderRequest request) {
        var customer = this.clientProxy.findCustomerById(request.getUserId())
                .orElseThrow(() -> new BusinessException("Cannot create order :: No customer found with ID: " + request.getToWardName()));


        this.productClientProxy.purchaseProducts(request.getPurchaseProducts());

        var order = this.orderRepository.save(createNewOrderEntity(request));

        for (PurchaseRequest purchaseRequest : request.getPurchaseProducts()) {
            orderLineRepository.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getOrderId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }





    }

    @Override
    public void captureTransactionPaypal(String paypalOrderId, String payerId) {

    }

    @Override
    public Integer createNewOrder(OrderRequest request) {
        return 0;
    }


}
