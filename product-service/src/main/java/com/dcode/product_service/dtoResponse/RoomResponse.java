package com.dcode.product_service.dtoResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomResponse {
    private String roomType;
    private String image;
    private String textUrl3D;
}
