package com.demo.sell_card_demo1.dto;

import lombok.Data;

@Data
public class AddVariantRequest {
    private Long value;
    private Long price;
    private String currency;
}
