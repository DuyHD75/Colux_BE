package com.dcode.product_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaintResponse {
    private String size;
    private String productName;
    private String description;
    private String price;
    private String ratingAverage;
    private String category;
    private String detail;
    private String placeOfOrigin;
    private String warranty;

}
