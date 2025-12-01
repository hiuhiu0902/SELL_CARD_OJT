package com.demo.sell_card_demo1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class RevenueStatsResponse {
    private LocalDate date;
    private Long dailyRevenue;
    private Long orderCount;
}