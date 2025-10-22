package com.demo.sell_card_demo1.dto;

import lombok.Data;

@Data
public class UpdateVariantRequest {
    private Long price;
    private String currency;
    private Long value;
}
