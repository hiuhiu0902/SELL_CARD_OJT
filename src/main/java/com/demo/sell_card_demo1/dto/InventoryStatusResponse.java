package com.demo.sell_card_demo1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryStatusResponse {
    private Long variantId;
    private String productName;
    private Long price;
    private Long quantityInStock;
}