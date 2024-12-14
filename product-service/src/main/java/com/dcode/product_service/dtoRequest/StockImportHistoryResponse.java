package com.dcode.product_service.dtoRequest;

import com.dcode.product_service.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockImportHistoryResponse {
    private String images;
    private String billCode;
    private String customerId;
    private String stockImportHistoryId;
    private UserResponse userInfo;
}
