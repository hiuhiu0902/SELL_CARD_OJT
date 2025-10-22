package com.demo.sell_card_demo1.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderItemRequest {
    public Long variantId;
    public int quantity;
}
