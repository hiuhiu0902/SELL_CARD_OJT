package com.demo.sell_card_demo1.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    public List<OrderItemRequest> orderItemRequests;
    public String paymentMethod;
}
