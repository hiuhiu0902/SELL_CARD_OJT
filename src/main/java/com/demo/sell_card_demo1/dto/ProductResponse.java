package com.demo.sell_card_demo1.dto;

import com.demo.sell_card_demo1.entity.Branch;

import java.util.List;

public class ProductResponse {
    public Long productId;
    public String productName;
    public String productDescription;
    public BranchResponse branch;
    public String pictureURL;
    public List<ProductVariantResponse> productVariantResponses;
}
