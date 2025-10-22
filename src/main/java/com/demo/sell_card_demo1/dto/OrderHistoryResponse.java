package com.demo.sell_card_demo1.dto;

import com.demo.sell_card_demo1.enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderHistoryResponse {
    private Long orderId;
    private OrderStatus orderStatus;
    private Long totalAmount;
    private LocalDateTime orderDate;
}
