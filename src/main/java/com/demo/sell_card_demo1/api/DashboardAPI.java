package com.demo.sell_card_demo1.api;

import com.demo.sell_card_demo1.service.StatisticService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@SecurityRequirement(name = "api")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardAPI {

    @Autowired
    private StatisticService statisticService;

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueStats(
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate fromDate,
            @RequestParam @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate toDate) {
        return ResponseEntity.ok(statisticService.getRevenueStats(fromDate, toDate));
    }
}