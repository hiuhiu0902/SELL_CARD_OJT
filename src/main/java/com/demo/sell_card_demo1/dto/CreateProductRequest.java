package com.demo.sell_card_demo1.dto;

import lombok.Data;

@Data
public class CreateProductRequest {
    private String name;
    private String description;
    private String pictureUrl;
    private String branchName;
    private String discountCode;
}
