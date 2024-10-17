package com.dcode.order_service.service.impl;

import com.dcode.order_service.dto.cart.request.CartRequest;
import com.dcode.order_service.entity.cart.CartEntity;
import com.dcode.order_service.service.ICartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements ICartService {
    @Override
    public void createClientCart(CartRequest request) {
        // TODO Auto-generated method stub
    }

    @Override
    public List<CartEntity> getAllCarts() {
        // TODO Auto-generated method stub
        return null;
    }

}
