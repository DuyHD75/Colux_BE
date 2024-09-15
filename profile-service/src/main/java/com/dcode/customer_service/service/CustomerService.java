package com.dcode.customer_service.service;

import com.dcode.customer_service.entity.Customer;
import com.dcode.customer_service.exception.CustomerExceptions;
import com.dcode.customer_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.*;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public String createCustomer(com.dcode.customer_service.dtorequest.ProfileRequest request) {
        var customer = repository.save(mapper.toCustomer(request));
        return customer.getId();
    }


    public void updateCustomer(com.dcode.customer_service.dtorequest.ProfileRequest request) {
        var customer = repository.findById(request.id()).orElseThrow(() -> new CustomerExceptions(
                format("Cannot find customer with ID :: %s", request.id())
        ));

        mergerCustomer(customer, request);

        repository.save(customer);

    }

    private void mergerCustomer(Customer customer, com.dcode.customer_service.dtorequest.ProfileRequest request) {
        if (StringUtils.isNotBlank(request.accumulatedPoints())) {
            customer.setAccumulatedPoints(request.accumulatedPoints());
        }
        if (request.address() != null) {
            customer.setAddress(request.address());
        }


    }

    public List<com.dcode.customer_service.dtoresponse.ProfileResponse> findAllCustomers() {
        return repository.findAll().stream().map(mapper::fromCustomer).collect(Collectors.toList());
    }

    public Boolean existsCustomerById(String customerId) {
        return repository.findById(customerId).isPresent();
    }

    public com.dcode.customer_service.dtoresponse.ProfileResponse findCustomerById(String customerId) {
        return repository.findById(customerId).map(mapper::fromCustomer)
                .orElseThrow(() -> new CustomerExceptions(
                format("Cannot find customer with ID :: %s", customerId)
        ));
    }

    public void deleteCustomerById(String customerId) {
        repository.deleteById(customerId);
    }
}
