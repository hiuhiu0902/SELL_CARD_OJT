package com.demo.sell_card_demo1.service;

import com.demo.sell_card_demo1.enums.OrderStatus;
import com.demo.sell_card_demo1.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatisticService {

    @Autowired
    private OrderRepository orderRepository;


// ...

    public Map<String, Object> getRevenueStats(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.atTime(LocalTime.MAX);

        Long totalRevenue = orderRepository.sumRevenue(from, to);
        long totalOrders = orderRepository.countByStatusAndCreatedAtBetween(OrderStatus.COMPLETED, from, to);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Map<String, Object> response = new HashMap<>();
        response.put("revenue", totalRevenue == null ? 0 : totalRevenue);
        response.put("orders", totalOrders);

        response.put("fromDate", from.format(formatter));
        response.put("toDate", to.format(formatter));

        return response;
    }
}