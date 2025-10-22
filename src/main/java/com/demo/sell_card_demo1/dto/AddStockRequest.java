package com.demo.sell_card_demo1.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AddStockRequest {
    private List<String> activationCodes;
    private LocalDateTime expirationDate;
    private LocalDateTime activationDate;
}
