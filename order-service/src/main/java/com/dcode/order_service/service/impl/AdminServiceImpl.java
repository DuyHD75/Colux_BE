package com.dcode.order_service.service.impl;

import com.dcode.order_service.domain.Response;
import com.dcode.order_service.dto.cart.request.CartVariantRequest;
import com.dcode.order_service.dto.dashboard.response.DashboardResponse;
import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.entity.order.OrderLineEntity;
import com.dcode.order_service.exception.BusinessException;
import com.dcode.order_service.proxy.ICustomerClientProxy;
import com.dcode.order_service.proxy.IProductClientProxy;
import com.dcode.order_service.repository.IOrderLineRepository;
import com.dcode.order_service.repository.IOrderRepository;
import com.dcode.order_service.repository.IWaybillRepository;
import com.dcode.order_service.service.IAdminService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements IAdminService {

    private final IOrderRepository orderRepository;
    private final IProductClientProxy productClientProxy;
    private final ICustomerClientProxy clientProxy;
    private final IOrderLineRepository orderLineRepository;
    private final IWaybillRepository waybillRepository;
    private final ObjectMapper objectMapper;

    @Override
    public DashboardResponse getDashboard() {
        DashboardResponse response = new DashboardResponse();
        try {
            // total customers
            DashboardResponse.CustomersDto customersDto = new DashboardResponse.CustomersDto();
            var customer = this.clientProxy.getTotalUser();
            customersDto.setType("customers");
            customersDto.setTitle("Tổng số khách hàng");
            customer.ifPresent(value -> customersDto.setValue((Integer) value.data().get("totalUser")));
            response.setCustomers(customersDto);

            response.setTotalOrder(orderRepository.count());
            response.setTotalShipping(waybillRepository.countByStatusIn(List.of(1, 2)));

            Optional<Response> responseProduct = this.productClientProxy.getDashboardInfo();
            if (responseProduct.isPresent() && responseProduct.get().data() != null) {
                List<Map<String, Object>> dataProduct =
                        objectMapper.convertValue(responseProduct.get().data().get("dashboard"), new TypeReference<List<Map<String, Object>>>() {
                        });
                int totalProduct = 0;
                int totalReview = 0;
                int totalBrand = 0;
                int totalSupplier = 0;

                for (Map<String, Object> data : dataProduct) {
                    if (data.containsKey("totalProduct")) {
                        totalProduct = (Integer) data.get("totalProduct");
                    }
                    if (data.containsKey("totalReview")) {
                        totalReview = (Integer) data.get("totalReview");
                    }
                    if (data.containsKey("totalBrand")) {
                        totalBrand = (Integer) data.get("totalBrand");
                    }
                    if (data.containsKey("totalSupplier")) {
                        totalSupplier = (Integer) data.get("totalSupplier");
                    }
                }
                response.setTotalProduct(totalProduct);
                response.setTotalReview(totalReview);
                response.setTotalBrand(totalBrand);
                response.setTotalSupplier(totalSupplier);

            }

            // top 5 product
            Pageable pageable = PageRequest.of(0, 5);
            response.setProducts(getTopProduct(pageable));



            // Calculate weekly revenue for this week and last week
            List<DashboardResponse.WeeklyRevenueDto> thisWeekData = calculateWeeklyRevenue(LocalDate.now());
            List<DashboardResponse.WeeklyRevenueDto> lastWeekData = calculateWeeklyRevenue(LocalDate.now().minusWeeks(1));
            response.setThisWeekData(thisWeekData);
            response.setLastWeekData(lastWeekData);

            // Transactions (orders) latest pageable
            List<OrderEntity> orders = orderRepository.findAll(PageRequest.of(0, 5)).toList();
            List<DashboardResponse.TransactionDto> transactionDtos = orders.stream()
                    .map(order -> {
                        DashboardResponse.TransactionDto transactionDto = new DashboardResponse.TransactionDto();
                        transactionDto.setId(order.getOrderId());
                        transactionDto.setCode(order.getCode());
                        transactionDto.setName(order.getToName()); // Điền tên khách hàng vào TransactionDto
                        transactionDto.setDate(order.getCreatedAt().toString());
                        transactionDto.setTotal(order.getTotalPay().toString());
                        transactionDto.setStatus(order.getStatus().toString());
                        transactionDto.setPaymentMethod(order.getPaymentMethod().toString());
                        return transactionDto;
                    })
                    .collect(Collectors.toList());
            response.setTransactions(transactionDtos);

            // Registration (new customers) data
            Optional<Response> registrations = this.clientProxy.monthlyUser(5);
            List<Map<String, Object>> registrationData = registrations
                    .map(responseData -> (List<Map<String, Object>>) responseData.data().get("monthlyUser"))
                    .orElse(Collections.emptyList());
            response.setRegistrations(registrationData.stream()
                    .map(data -> {
                        DashboardResponse.RegistrationDto registrationDto = new DashboardResponse.RegistrationDto();
                        registrationDto.setMonth((String) data.get("month"));
                        registrationDto.setRegistrations((Integer) data.get("userCount"));
                        registrationDto.setYear((Integer) data.get("year"));
                        return registrationDto;
                    })
                    .collect(Collectors.toList()));


            // Lấy thời điểm đầu tháng cách đây 8 tháng và hiện tại
            LocalDate startDate = LocalDate.now().minusMonths(8);
            LocalDate endDate = LocalDate.now();

            // Tạo danh sách các tháng từ 8 tháng trước đến tháng hiện tại
            List<String> allMonths = new ArrayList<>();
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusMonths(1)) {
                String monthYear = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
                allMonths.add(monthYear);
            }

            // Lấy doanh thu từ cơ sở dữ liệu
            List<Object[]> results = orderRepository.getRevenueForOrders(startDate.atStartOfDay());

            Map<String, BigDecimal> revenueMap = new HashMap<>();

            // Gán doanh thu vào map theo tháng
            for (Object[] result : results) {
                LocalDateTime createdAt = (LocalDateTime) result[0];
                BigDecimal totalPay = (BigDecimal) result[1];

                // Chuyển ngày tháng của đơn hàng thành chuỗi "YYYY-MM"
                String monthYear = createdAt.getYear() + "-" + String.format("%02d", createdAt.getMonthValue());

                // Cộng doanh thu vào map theo tháng
                revenueMap.put(monthYear, revenueMap.getOrDefault(monthYear, BigDecimal.ZERO).add(totalPay));
            }

            // Đảm bảo trả về đủ tất cả các tháng, nếu không có doanh thu thì gán là 0
            List<DashboardResponse.MonthlyRevenueDto> monthlyRevenueDtos = allMonths.stream()
                    .map(month -> {
                        DashboardResponse.MonthlyRevenueDto monthlyRevenueDto = new DashboardResponse.MonthlyRevenueDto();
                        monthlyRevenueDto.setMonth(month);  // Set "YYYY-MM"

                        // Gán doanh thu, nếu không có thì gán là 0
                        monthlyRevenueDto.setRevenue(revenueMap.getOrDefault(month, BigDecimal.ZERO).intValue());

                        monthlyRevenueDto.setProfit(0);  // Giả sử lợi nhuận là 0 nếu không có tính toán

                        return monthlyRevenueDto;
                    })
                    .collect(Collectors.toList());

            // Trả về danh sách doanh thu theo tháng
            response.setMonthlyData(monthlyRevenueDtos);






            // Get the current date and time (end date is the current date/time)
            LocalDateTime endDateCategory = LocalDateTime.now();

            // Start date is 12 months ago
            LocalDateTime startDateCategory = endDateCategory.minusMonths(12);

            // Set the start of the day for startDate and the end of the day for endDateCategory
            LocalDateTime startDateTime = startDateCategory.with(LocalTime.MIDNIGHT); // Start of the day for startDate (00:00:00)
            LocalDateTime endDateTime = endDateCategory.with(LocalTime.MAX); // End of the day for endDateCategory (23:59:59.999999999)

            // Retrieve order lines for the date range
            List<OrderLineEntity> orderLines = orderLineRepository.findOrderLinesByDateRange(startDateTime, endDateTime);

            // List to store MonthlyRevenueDataDto
            List<DashboardResponse.MonthlyRevenueDataDto> monthlyRevenueDataList = new ArrayList<>();

            for (int i = 0; i < 12; i++) {
                // Start of the current month and end of the current month
                LocalDateTime currentMonthStart = startDateTime.plusMonths(i);
                LocalDateTime currentMonthEnd = currentMonthStart.plusMonths(1).minusSeconds(1); // Last second of the current month

                // Filter the order lines for the current month
                List<OrderLineEntity> currentMonthOrderLines = orderLines.stream()
                        .filter(orderLine -> orderLine.getCreatedAt().isAfter(currentMonthStart) &&
                                orderLine.getCreatedAt().isBefore(currentMonthEnd))
                        .collect(Collectors.toList());

                // Count the quantities of each product type for the current month
                int paints = 0;
                int wallpapers = 0;
                int floors = 0;

                for (OrderLineEntity orderLine : currentMonthOrderLines) {
                    // Check the IDs to determine the product type
                    if (orderLine.getPaintId() != null) {
                        paints += orderLine.getQuantity();
                    } else if (orderLine.getWallpaperId() != null) {
                        wallpapers += orderLine.getQuantity();
                    } else if (orderLine.getFloorId() != null) {
                        floors += orderLine.getQuantity();
                    }
                }

                // Create a MonthlyRevenueDataDto for the current month
                DashboardResponse.MonthlyRevenueDataDto dataDto = new DashboardResponse.MonthlyRevenueDataDto();
                dataDto.setMonth(currentMonthStart.getMonth().toString() + " " + currentMonthStart.getYear());
                dataDto.setPaints(paints);
                dataDto.setWallpaper(wallpapers);
                dataDto.setFloor(floors);

                monthlyRevenueDataList.add(dataDto);
            }
        response.setMonthlyRevenueData(monthlyRevenueDataList);

        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }

        return response;
    }

    public List<DashboardResponse.ProductDto> getTopProduct(Pageable pageable){
        // top 5 products
        List<Map<String, Object>> topProducts = this.orderLineRepository.findTop5MostFrequentProductsWithQuantity(pageable);
        List<CartVariantRequest> productRequests = topProducts.stream().map(
                product -> {
                    CartVariantRequest cartVariantRequest = new CartVariantRequest();
                    cartVariantRequest.setProductId((String) product.get("productId"));
                    return cartVariantRequest;
                }
        ).toList();

        var productsInfo = this.productClientProxy.getProductDashboard(productRequests);

        // Extract product list from productsInfo response
        List<Map<String, Object>> productsData = productsInfo
                .map(responseData -> (List<Map<String, Object>>) responseData.data().get("products"))
                .orElse(Collections.emptyList());

        List<DashboardResponse.ProductDto> productDtos = topProducts.stream()
                .map(topProduct -> {
                    // Find the corresponding product in productsData by productId
                    String productId = (String) topProduct.get("productId");
                    Map<String, Object> productInfo = productsData.stream()
                            .filter(info -> productId.equals(info.get("productId")))
                            .findFirst()
                            .orElse(null);

                    // Build ProductDto if productInfo exists
                    if (productInfo != null) {
                        DashboardResponse.ProductDto productDto = new DashboardResponse.ProductDto();
                        productDto.setName((String) productInfo.get("productName"));

                        // Use the first image URL if available
                        List<Map<String, Object>> images = (List<Map<String, Object>>) productInfo.get("images");
                        if (images != null && !images.isEmpty()) {
                            productDto.setImage((String) images.get(0).get("url"));
                        }

                        productDto.setCategory((String) ((Map<String, Object>) productInfo.get("category")).get("name"));
                        productDto.setSold((long) topProduct.get("totalQuantity")); // quantity sold
//                            productDto.setProfit(productDto.getPrice() * productDto.getSold()); // calculate profit

                        return productDto;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return productDtos;
    }

    private List<DashboardResponse.WeeklyRevenueDto> calculateWeeklyRevenue(LocalDate startDate) {
        List<DashboardResponse.WeeklyRevenueDto> weeklyRevenueDtos = new ArrayList<>();

        // Convert LocalDate to LocalDateTime (start of the day)
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = startDate.plusDays(7).atStartOfDay(); // Assuming you're using a range of 7 days

        // Fetch orders based on the date range and conditions for paymentStatus and order status
        List<OrderEntity> orders = orderRepository.findByDateRangeAndStatusAndPaymentStatus(
                startDateTime,
                endDateTime,
                Collections.singletonList(2), // 2 = paid status
                Arrays.asList(1, 2, 3) // example: include orders with status 1 (processing), 2 (shipped), and 3 (completed)
        );

        // Initialize the weekly revenue list for each day of the week
        Map<String, BigDecimal> dailyRevenue = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            String dayOfWeek = startDate.plusDays(i).getDayOfWeek().toString().substring(0, 3);  // Get short day name (Mon, Tue, ...)
            dailyRevenue.put(dayOfWeek, BigDecimal.ZERO);
        }

        // Sum totalPay for each order based on the day of the week
        for (OrderEntity order : orders) {
            LocalDateTime orderDateTime = order.getCreatedAt(); // Assuming 'createdAt' is LocalDateTime
            String dayOfWeek = orderDateTime.getDayOfWeek().toString().substring(0, 3);  // Get short day name (Mon, Tue, ...)

            dailyRevenue.put(dayOfWeek, dailyRevenue.get(dayOfWeek).add(order.getTotalPay()));
        }

        // Convert daily revenue map to WeeklyRevenueDto list
        for (Map.Entry<String, BigDecimal> entry : dailyRevenue.entrySet()) {
            DashboardResponse.WeeklyRevenueDto dto = new DashboardResponse.WeeklyRevenueDto();
            dto.setDay(entry.getKey());
            dto.setRevenue(entry.getValue().intValue());
            weeklyRevenueDtos.add(dto);
        }

        return weeklyRevenueDtos;
    }
}
