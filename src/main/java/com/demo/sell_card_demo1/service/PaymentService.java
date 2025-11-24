package com.demo.sell_card_demo1.service;

import com.demo.sell_card_demo1.entity.Order;
import com.demo.sell_card_demo1.entity.OrderItem;
import com.demo.sell_card_demo1.entity.Transaction;
import com.demo.sell_card_demo1.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AuthenticationService authenticationService;
    @Value("${payos.return-url}")
    private String returnUrl;

    @Value("${payos.cancel-url}")
    private String cancelUrl;

    @Transactional
    public String createPaymentLink(Order order) throws Exception {
        List<PaymentLinkItem> payOsItems = new ArrayList<>();
        for (OrderItem item : order.getOrderItems()) {
            PaymentLinkItem payOsItem = PaymentLinkItem.builder()
                    .name(item.getProduct().getName())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .build();
            payOsItems.add(payOsItem);
        }
        String description = "Payment for order #" + order.getOrderId();
        CreatePaymentLinkRequest request = CreatePaymentLinkRequest.builder()
                .orderCode(order.getOrderId())
                .amount(order.getTotalAmount())
                .description(description)
                .items(payOsItems)
                .cancelUrl(cancelUrl)
                .returnUrl(returnUrl)
                .build();
        CreatePaymentLinkResponse response = payOS.paymentRequests().create(request);
        return response.getCheckoutUrl();
    }

}
