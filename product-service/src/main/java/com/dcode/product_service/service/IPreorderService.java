package com.dcode.product_service.service;

import com.dcode.product_service.dtoRequest.PreorderRequest;
import com.dcode.product_service.dtoResponse.PreorderResponse;

public interface IPreorderService {
    void createPreorder(PreorderRequest preorderRequest);

    PreorderResponse getAPreorder(String preorderId);

}
