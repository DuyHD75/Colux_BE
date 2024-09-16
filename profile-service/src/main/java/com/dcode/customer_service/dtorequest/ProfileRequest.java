package com.dcode.customer_service.dtorequest;

import com.dcode.customer_service.entity.Address;

public record ProfileRequest(
        String id,
        String accumulatedPoints,
        Address address
) {
}
