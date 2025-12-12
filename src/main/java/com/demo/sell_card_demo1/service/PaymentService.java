package com.demo.sell_card_demo1.service;

import com.demo.sell_card_demo1.entity.Order;
import com.demo.sell_card_demo1.entity.OrderItem;
import com.demo.sell_card_demo1.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    @Autowired
    private PayOS payOS;
    // @Autowired TransactionRepository transactionRepository; // Bỏ nếu chưa dùng tới để code gọn
    private String cancelUrl = "https://sellcardojt.site/api/payment/cancel";
    private String returnUrl = "https://sellcardojt.site/api/payment/success";

    public String createPaymentLink(Order order) throws Exception {
        // 1. Map OrderItems sang PayOS Items
        List<PaymentLinkItem> payOsItems = new ArrayList<>();
        for (OrderItem item : order.getOrderItems()) {
            PaymentLinkItem payOsItem = PaymentLinkItem.builder()
                    .name(item.getProduct().getName()) // Lưu ý: Tên quá dài có thể gây lỗi, nên cắt ngắn nếu cần
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .build();
            payOsItems.add(payOsItem);
        }

        // 2. Xử lý Description: PayOS yêu cầu tối đa 25 ký tự, không dấu, ko ký tự đặc biệt
        // Format an toàn: "DH [Mã đơn]"
        String description = "DH " + order.getOrderId();

        // 3. Tạo request
        CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                .orderCode(order.getOrderId()) // orderId phải nằm trong khoảng giới hạn của PayOS (Long)
                .amount(order.getTotalAmount())
                .description(description)
                .items(payOsItems)
                .cancelUrl(cancelUrl)
                .returnUrl(returnUrl)
                .build();

        // 4. Gọi API
        CreatePaymentLinkResponse response = payOS.paymentRequests().create(request);
        return response.getCheckoutUrl();
    }
}