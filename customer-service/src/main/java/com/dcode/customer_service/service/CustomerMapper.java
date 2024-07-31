package com.dcode.customer_service.service;

import com.dcode.customer_service.dtorequest.CustomerRequest;
import com.dcode.customer_service.dtoresponse.CustomerResponse;
import com.dcode.customer_service.entity.Customer;
import org.springframework.stereotype.Service;

@Service
public class CustomerMapper {
    public Customer toCustomer(CustomerRequest request) {
        if (request == null) {
            return null;
        }
        return Customer.builder()
                .id(request.id())
                .accumulatedPoints(request.accumulatedPoints())
                .address(request.address())
                .build();
    }

    public CustomerResponse fromCustomer(Customer customer) {
        return new CustomerResponse(
                customer.getId(), customer.getAccumulatedPoints(), customer.getAddress()
        );
    }
}
