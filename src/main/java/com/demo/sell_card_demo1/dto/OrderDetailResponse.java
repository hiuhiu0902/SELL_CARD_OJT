package com.demo.sell_card_demo1.dto;

import com.demo.sell_card_demo1.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailResponse {
    private Long orderId;
    private String payment;
    private OrderStatus orderStatus;
    private Long totalAmount;
    private LocalDateTime orderDate; // Admin cần biết ngày giờ chính xác

    private Long userId;
    private String username;
    private Long transactionCode;

    private List<PurchasedItemResponse> purchasedItems;
}
