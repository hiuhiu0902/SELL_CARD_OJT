package com.demo.sell_card_demo1.dto;

import com.demo.sell_card_demo1.entity.Product;
import com.demo.sell_card_demo1.entity.ProductVariant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AddProductRequest {
    public String name;
    public String description;
    public String pictureUrl;
    public String branchName;
    public String discountCode;
    public Double variantValue;
    public Double price;
    public String currency;
    public String activateCode;
    public LocalDateTime expirationDate;
    public LocalDateTime activationDate;
}
