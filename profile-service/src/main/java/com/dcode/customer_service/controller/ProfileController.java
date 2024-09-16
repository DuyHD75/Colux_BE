package com.dcode.customer_service.controller;

import com.dcode.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class ProfileController {

    private final CustomerService customerService;

    @PostMapping()
    public ResponseEntity<String> createCustomer(@RequestBody @Valid com.dcode.customer_service.dtorequest.ProfileRequest request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @PutMapping()
    public ResponseEntity<Void> updateCustomer(@RequestBody @Valid com.dcode.customer_service.dtorequest.ProfileRequest request) {
        customerService.updateCustomer(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<com.dcode.customer_service.dtoresponse.ProfileResponse>> getCustomers() {
        return ResponseEntity.ok(customerService.findAllCustomers());
    }

    @GetMapping("/exits/{customer-id}")
    public ResponseEntity<Boolean> existsCustomer(@PathVariable("customer-id") String customerId) {
        return ResponseEntity.ok(customerService.existsCustomerById(customerId));
    }


    @GetMapping("/{customer-id}")
    public ResponseEntity<com.dcode.customer_service.dtoresponse.ProfileResponse> findCustomerById(@PathVariable("customer-id") String customerId) {
        return ResponseEntity.ok(customerService.findCustomerById(customerId));
    }


    @DeleteMapping("/{customer-id}")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable("customer-id") String customerId) {
        customerService.deleteCustomerById(customerId);
        return ResponseEntity.accepted().build();
    }


}
