package com.dcode.customer_service.dtoresponse;


import com.dcode.customer_service.entity.Address;

public record ProfileResponse(String id,
                              String accumulatedPoints,
                              Address address) {
}
