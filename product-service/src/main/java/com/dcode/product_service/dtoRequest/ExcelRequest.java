package com.dcode.product_service.dtoRequest;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
public class ExcelRequest {
    private String images; // Dữ liệu mã hóa hình ảnh
    private String billCode; // Mã hóa đơn
    private Set<ProductExcelRequest> products; // Danh sách sản phẩm

    @Data
    public static class ProductExcelRequest {
        private String code;
        private String description;
        private String placeOfOrigin;
        private String productName;
        private String supplierId;
        private Double ratingAverage;
        private String warranty;
        private String applicableSurface;
        private String brandCode;
        private Set<String> featureValueIds;
        private Set<String> propertyValueIds;
        private Set<String> images;
        private ColorRequest color;
        private Double foamThickness;
        private Integer numberPiecesPerBox;
        private Integer quantity;
        private Double price; // phải do Employee hoặc Admin nhập - sẽ được nhập sau khi kh gửi file excel
        // variant
        private String categoryName;
        private String sizeName;
        private String packageType;
    }
}

