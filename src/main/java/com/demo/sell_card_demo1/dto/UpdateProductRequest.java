package com.demo.sell_card_demo1.dto;

import lombok.Data;

@Data
public class UpdateProductRequest {
    private String name;
    private String description;
    private String imageUrl;
    private String branchName;
    private String discountCode;
}
