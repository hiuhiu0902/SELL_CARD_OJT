package com.demo.sell_card_demo1.dto;

import com.demo.sell_card_demo1.enums.OrderStatus;
import lombok.Data;

import java.util.List;

@Data
public class OrderDetailResponse {
    public Long orderId;
    public String payment;
    public OrderStatus orderStatus;
    public Long totalAmount;
    public List<PurchasedItemResponse> purchasedItems;
}
