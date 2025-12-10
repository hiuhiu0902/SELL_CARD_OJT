package com.demo.sell_card_demo1.api;

import com.demo.sell_card_demo1.dto.OrderDetailResponse;
import com.demo.sell_card_demo1.dto.OrderHistoryResponse;
import com.demo.sell_card_demo1.entity.Order;
import com.demo.sell_card_demo1.enums.OrderStatus;
import com.demo.sell_card_demo1.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/orders")
@SecurityRequirement(name = "api")
@PreAuthorize("hasRole('ADMIN')")
public class OrderManageAPI {

    @Autowired
    private OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> getOrderById(@PathVariable Long orderId) {
        OrderDetailResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus status) {
        OrderDetailResponse updatedOrder = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(updatedOrder);
    }


    /**
     * THÊM: Tìm kiếm nâng cao thay vì chỉ getAllOrders.
     * Admin cần tìm: "Đơn của thằng A hôm qua bị lỗi".
     */
    @GetMapping
    public ResponseEntity<Page<OrderHistoryResponse>> searchOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) OrderStatus status,
            @ParameterObject Pageable pageable) {
        Page<OrderHistoryResponse> orders = orderService.searchOrders(from, to, username, status, pageable);
        return ResponseEntity.ok(orders);
    }

    /**
     * THÊM: Hoàn tiền đơn hàng.
     * Khi khách báo thẻ lỗi, Admin cần nút này để trả lại tiền và thu hồi thẻ.
     */
    @PostMapping("/{orderId}/refund")
    public ResponseEntity<String> refundOrder(@PathVariable Long orderId) {
        orderService.refundOrder(orderId);
        return ResponseEntity.ok("Order has been refunded and cards returned/marked as error.");
    }
}