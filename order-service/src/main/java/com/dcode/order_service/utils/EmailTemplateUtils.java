package com.dcode.order_service.utils;

import com.dcode.order_service.dto.cart.response.CartVariantResponse;
import com.dcode.order_service.dto.order.Order;
import com.dcode.order_service.entity.order.OrderEntity;
import com.dcode.order_service.entity.order.OrderLineEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EmailTemplateUtils {

    public static String createOrderConfirmationEmail(OrderEntity order, List<CartVariantResponse.ClientVariantResponse> productLines, Map<?, ?> data) {

        String items = mapProductLineToString(productLines, order);

        return String.format(
                "Subject: 🎉 Order Confirmation %s - Thank You for Shopping with Us!\n\n" +
                        "Hello %s,\n\n" +
                        "Thank you for choosing Colux Alpha! Your order has been successfully placed, and we’re getting it ready. " +
                        "Below are the details of your order:\n\n" +
                        "---\n\n" +
                        "Order Information:\n" +
                        "- Order Code: %s\n" +
                        "- Order Date: %s\n\n" +
                        "Items Ordered:\n" +
                        "%s\n" +
                        "Subtotal: %s\n" +
                        "Shipping Fee: %s\n" +
                        "Advance Payment: %s\n" +
                        "Total Payable: %s\n\n" +
                        "---\n\n" +
                        "Shipping Address:\n%s\n\n" +
                        "---\n\n" +
                        "What Happens Next?\n\n" +
                        "1. Order Processing: Your order is currently being prepared. We’ll update you as soon as it’s ready for shipping.\n" +
                        "2. Shipping Confirmation: Once your order ships, you’ll receive an email with tracking information to easily monitor the delivery status.\n\n" +
                        "If you have any questions, please reach out to us at [coluxalpha@gmail.com]. Our team is here to help!\n\n" +
                        "Thank you once again for shopping with Colux Alpha. We hope you enjoy your purchase!\n\n" +
                        "Best regards,\n" +
                        "Colux Alpha\n",
                order.getCode(), order.getToName(), order.getCode(), order.getCreatedAt(),
                items, order.getTotalAmount(), order.getShippingCost(), order.getAdvancePayment(),
                order.getTotalPay(), mapAddressToString(order)
        );
    }


    public static String createOrderCancellationEmail(OrderEntity order, List<CartVariantResponse.ClientVariantResponse> productLines, Map<?, ?> data) {
        String items = mapProductLineToString(productLines, order);

        return String.format(
                "Subject: ❌ Order Cancellation %s - We’re Sorry to See You Go\n\n" +
                        "Hello %s,\n\n" +
                        "We’re reaching out to inform you that your order with Colux Alpha has been successfully canceled. " +
                        "Below are the details of the canceled order:\n\n" +
                        "---\n\n" +
                        "Order Information:\n" +
                        "- Order Code: %s\n" +
                        "- Order Date: %s\n\n" +
                        "Items Ordered:\n" +
                        "%s\n" +
                        "Subtotal: %s\n" +
                        "Shipping Fee: %s\n" +
                        "Advance Payment (if any): %s\n" +
                        "Total Amount: %s\n\n" +
                        "---\n\n" +
                        "Shipping Address (if applicable):\n%s\n\n" +
                        "---\n\n" +
                        "Refund Information:\n\n" +
                        "If you made any payments, a refund will be processed to your original payment method within the next few business days.\n\n" +
                        "If you have any questions or concerns about your order or the refund process, please feel free to reach out to us at [coluxalpha@gmail.com]. " +
                        "Our support team is always here to assist you.\n\n" +
                        "Thank you for considering Colux Alpha. We hope to have the opportunity to serve you in the future.\n\n" +
                        "Best regards,\n" +
                        "Colux Alpha\n",
                order.getCode(), order.getToName(), order.getCode(), order.getCreatedAt(),
                items, order.getTotalAmount(), order.getShippingCost(), order.getAdvancePayment(),
                order.getTotalPay(), mapAddressToString(order)
        );
    }

    public static String createOrderCompletionEmail(OrderEntity order, List<CartVariantResponse.ClientVariantResponse> productLines, Map<?, ?> data) {
        String items = mapProductLineToString(productLines, order);

        return String.format(
                "Subject: ✅ Order Completion %s - Your Order Has Shipped Successfully!\n\n" +
                        "Hello %s,\n\n" +
                        "We’re excited to let you know that your order with Colux Alpha has been successfully shipped! " +
                        "Below are the details of your order and tracking information to help you monitor its journey:\n\n" +
                        "---\n\n" +
                        "Order Information:\n" +
                        "- Order Code: %s\n" +
                        "- Order Date: %s\n\n" +
                        "Items Ordered:\n" +
                        "%s\n" +
                        "Subtotal: %s\n" +
                        "Shipping Fee: %s\n" +
                        "Advance Payment: %s\n" +
                        "Total Amount: %s\n\n" +
                        "---\n\n" +
                        "Shipping Address:\n%s\n\n" +
                        "Tracking Information:\n" +
                        "- Carrier: %s\n" +
                        "- Tracking Number: %s\n" +
                        "- Estimated Delivery Date: %s\n\n" +
                        "---\n\n" +
                        "Thank you for shopping with Colux Alpha! We hope you love your purchase. If you have any questions about your order, " +
                        "please reach out to us at [coluxalpha@gmail.com]. Our team is always here to assist.\n\n" +
                        "Best regards,\n" +
                        "Colux Alpha\n",
                order.getCode(), order.getToName(), order.getCode(), order.getCreatedAt(),
                items, order.getTotalAmount(), order.getShippingCost(), order.getAdvancePayment(),
                order.getTotalPay(), mapAddressToString(order),
                data.get("carrier"), data.get("trackingNumber"), data.get("estimatedDeliveryDate")
        );
    }

    private static String mapProductLineToString(List<CartVariantResponse.ClientVariantResponse> productLines, OrderEntity order) {
        StringBuilder itemsList = new StringBuilder();
        for (CartVariantResponse.ClientVariantResponse productLine : productLines) {
            for (OrderLineEntity orderLine : order.getOrderLines()) {
                if (Objects.equals(orderLine.getVariantId(), productLine.getVariantId())) {
                    itemsList.append("- ")
                            .append(productLine.getProductDetails().getProductName())
                            .append("        ")
                            .append(" x ")
                            .append(orderLine.getQuantity())
                            .append(productLine.getPriceSell())
                            .append("\n");
                }
            }
        }
        return itemsList.toString();
    }

    private static String mapAddressToString(OrderEntity entity) {
        StringBuilder address = new StringBuilder();
        address.append(entity.getToAddress()).append(", ")
                .append(entity.getToWardName()).append(", ")
                .append(entity.getToDistrictName()).append(", ")
                .append(entity.getToProvinceName());
        return address.toString();
    }

}
