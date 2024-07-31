package com.dcode.customer_service.controller;

import com.dcode.customer_service.dtorequest.CustomerRequest;
import com.dcode.customer_service.dtoresponse.CustomerResponse;
import com.dcode.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping()
    public ResponseEntity<String> createCustomer(@RequestBody @Valid CustomerRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @PutMapping()
    public ResponseEntity<Void> updateCustomer(@RequestBody @Valid CustomerRequest request) {
        customerService.updateCustomer(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getCustomers() {
        return ResponseEntity.ok(customerService.findAllCustomers());
    }

    @GetMapping("/exits/{customer-id}")
    public ResponseEntity<Boolean> existsCustomer(@PathVariable("customer-id") String customerId) {
        return ResponseEntity.ok(customerService.existsCustomerById(customerId));
    }


    @GetMapping("/{customer-id}")
    public ResponseEntity<CustomerResponse> findCustomerById(@PathVariable("customer-id") String customerId) {
        return ResponseEntity.ok(customerService.findCustomerById(customerId));
    }


    @DeleteMapping("/{customer-id}")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable("customer-id") String customerId) {
        customerService.deleteCustomerById(customerId);
        return ResponseEntity.accepted().build();
    }


}
