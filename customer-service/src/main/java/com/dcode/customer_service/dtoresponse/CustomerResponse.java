package com.dcode.customer_service.dtoresponse;


import com.dcode.customer_service.entity.Address;

public record CustomerResponse(String id,
                               String accumulatedPoints,
                               Address address) {
}
