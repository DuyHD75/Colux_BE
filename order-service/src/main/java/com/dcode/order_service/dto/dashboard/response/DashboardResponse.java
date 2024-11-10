package com.dcode.order_service.dto.dashboard.response;

import lombok.Data;

import java.util.List;

@Data
public class DashboardResponse {

    private CustomersDto customers;
    private Integer totalProduct;
    private long totalOrder;
    private long totalShipping;
    private Integer totalReview;
    private Integer totalBrand;
    private Integer totalSupplier;
    private List<ProductDto> products;
    private List<WeeklyRevenueDto> thisWeekData;
    private List<WeeklyRevenueDto> lastWeekData;
    private List<TransactionDto> transactions;
    private List<MonthlyRevenueDto> monthlyData;
    private List<RegistrationDto> registrations;

    @Data
    public static class CustomersDto {
        private String type;
        private String title;
        private Integer value;
    }

    @Data
    public static class ProductDto {
        private String name;
        private String image;
        private String category;
        private Integer price;
        private long sold;
        private Integer profit;
    }

    @Data
    public static class WeeklyRevenueDto {
        private String day;
        private Integer revenue;
    }

    @Data
    public static class TransactionDto {
        private String id;
        private String code;
        private String name;
        private String date;
        private String total;
        private String status;
        private String paymentMethod;
    }

    @Data
    public static class MonthlyRevenueDto {
        private String month;
        private Integer revenue;
        private Integer profit;
    }

    @Data
    public static class RegistrationDto {
        private String month;
        private Integer registrations;
        private Integer year;
    }
}
