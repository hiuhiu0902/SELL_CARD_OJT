package com.demo.sell_card_demo1.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailDetail {
    private String recipient;
    private String subject;
    private String link;
}
