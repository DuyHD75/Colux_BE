package com.dcode.product_service.dtoRequest;

import lombok.Data;

@Data
public class RoomRequest {
    private String roomType;
    private String hex;
    private String title;
    private String description;
    private String image;
    private String textUrl3D;
}
