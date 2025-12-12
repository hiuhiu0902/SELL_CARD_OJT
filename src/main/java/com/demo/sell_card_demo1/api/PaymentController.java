package com.demo.sell_card_demo1.api;

import com.demo.sell_card_demo1.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private OrderService orderService;

    @Value("${frontend.url}")
    private String frontendUrl;

    // 1. Xử lý khi thanh toán THÀNH CÔNG
    @GetMapping("/success")
    public void handleSuccess(@RequestParam("orderCode") Long orderCode,
                              HttpServletResponse response) throws IOException {
        try {
            // Cập nhật DB thành COMPLETED
            orderService.handlePaymentSuccess(orderCode);

            // Redirect về trang Success của Frontend
            response.sendRedirect(frontendUrl + "/order/success");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(frontendUrl + "/order/error");
        }
    }

    // 2. Xử lý khi người dùng bấm HỦY (CANCEL)
    @GetMapping("/cancel")
    public void handleCancel(@RequestParam("orderCode") Long orderCode,
                             HttpServletResponse response) throws IOException {
        try {
            // Cập nhật DB thành FAILED và nhả kho
            orderService.handlePaymentCancel(orderCode);

            // Redirect về trang Cancelled của Frontend
            response.sendRedirect(frontendUrl + "/order/cancelled");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(frontendUrl + "/order/error");
        }
    }
}