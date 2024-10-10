package com.dcode.customer_service.service;

import com.dcode.customer_service.entity.Customer;
import org.springframework.stereotype.Service;

@Service
public class CustomerMapper {
    public Customer toCustomer(com.dcode.customer_service.dtorequest.ProfileRequest request) {
        if (request == null) {
            return null;
        }
        return Customer.builder()
                .id(request.id())
                .accumulatedPoints(request.accumulatedPoints())
                .address(request.address())
                .build();
    }

    public com.dcode.customer_service.dtoresponse.ProfileResponse fromCustomer(Customer customer) {
        return new com.dcode.customer_service.dtoresponse.ProfileResponse(
                customer.getId(), customer.getAccumulatedPoints(), customer.getAddress()
        );
    }
}
