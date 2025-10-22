package com.demo.sell_card_demo1.dto;

import lombok.Data;

import java.util.List;

@Data
public class PurchasedItemResponse {
    private String productName;
    private int quantity;
    private Long pricePerUnit;
    List<CardInfo> cards;
}
