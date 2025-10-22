package com.demo.sell_card_demo1.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardInfo {
    private String serial;
    private String code;
    private LocalDateTime expirationDate;
}
