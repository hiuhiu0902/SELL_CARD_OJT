package com.demo.sell_card_demo1.api;

import com.demo.sell_card_demo1.dto.CreateOrderRequest;
import com.demo.sell_card_demo1.dto.CreatePaymentResponse;
import com.demo.sell_card_demo1.dto.OrderDetailResponse;
import com.demo.sell_card_demo1.dto.OrderHistoryResponse;
import com.demo.sell_card_demo1.entity.Order;
import com.demo.sell_card_demo1.repository.OrderRepository;
import com.demo.sell_card_demo1.service.OrderService;
import com.demo.sell_card_demo1.service.PaymentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;

import java.util.List;

@RestController
@RequestMapping("/order")
@SecurityRequirement(
        name = "api"
)
public class OrderAPI {
    @Autowired
    OrderService orderService;
    @Autowired
    PaymentService paymentService;
//    @PostMapping("/create-order")
//    public ResponseEntity createOrder(CreateOrderRequest createOrderRequest) {
//        orderService.createOrder(createOrderRequest);
//        return ResponseEntity.ok("Create order");
//    }
    @PostMapping("/create")
    public ResponseEntity<?> createOrderAndPaymentLink(@RequestBody CreateOrderRequest createOrderRequest) {
        try {
            // Bước 1: Tạo đơn hàng trong database
            Order newOrder = orderService.createOrder(createOrderRequest);

            // Bước 2: Dùng đơn hàng vừa tạo để lấy link thanh toán
            String paymentLink = paymentService.createPaymentLink(newOrder);

            // Bước 3: Đóng gói link vào một object và trả về cho client
            CreatePaymentResponse response = new CreatePaymentResponse();
            response.setPaymentUrl(paymentLink);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Trả về lỗi nếu có vấn đề (ví dụ: hết hàng)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/history")
    public ResponseEntity getOrderHistory(Pageable pageable) {
        List<OrderHistoryResponse> responses =  orderService.getOrderHistoryForCurrentUser(pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{orderId}" )
    public ResponseEntity getOrderDetailById(@PathVariable Long orderId) {
        OrderDetailResponse response = orderService.getOrderDetailForCurrentUser(orderId);
        return ResponseEntity.ok(response);
    }
}
