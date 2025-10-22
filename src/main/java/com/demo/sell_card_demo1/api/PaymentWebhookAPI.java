package com.demo.sell_card_demo1.api;

import com.demo.sell_card_demo1.dto.WebhookRequest;
import com.demo.sell_card_demo1.service.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payment-webhook")
public class PaymentWebhookAPI {

    private final OrderService orderService;
    private final String checksumKey;

    @Autowired
    public PaymentWebhookAPI(OrderService orderService, @Value("${payos.checksum-key}") String checksumKey) {
        this.orderService = orderService;
        this.checksumKey = checksumKey;
    }

    @PostMapping("/payos")
    public ResponseEntity<String> handlePayOSWebhook(@RequestBody WebhookRequest request) {
        try {
            // Bước 1: Tự xác thực chữ ký
            if (!verifySignature(request.getData(), request.getSignature())) {
                System.err.println("Webhook signature verification failed!");
                return ResponseEntity.status(401).body("Invalid signature");
            }

            // Bước 2: Lấy dữ liệu
            JsonNode data = request.getData();
            long orderId = data.get("orderCode").asLong();
            String status = data.get("code") != null ? data.get("code").asText() : "UNKNOWN";

            // Bước 3: Xử lý logic nghiệp vụ
            if ("PAID".equals(status)) {
                orderService.handlePaymentSuccess(orderId);
            } else {
                orderService.handlePaymentFailure(orderId);
            }
            return ResponseEntity.ok("Webhook handled successfully");

        } catch (Exception e) {
            System.err.println("Webhook handling failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("Webhook handling failed");
        }
    }

    // HÀM TỰ XÂY DỰNG ĐỂ XÁC THỰC CHỮ KÝ HMAC-SHA256
    private boolean verifySignature(JsonNode data, String receivedSignature) throws NoSuchAlgorithmException, InvalidKeyException {
        List<String> keys = new ArrayList<>();
        Iterator<String> fieldNames = data.fieldNames();
        while (fieldNames.hasNext()) {
            keys.add(fieldNames.next());
        }
        Collections.sort(keys);

        StringBuilder dataToSign = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = data.get(key).asText();
            dataToSign.append(key).append("=").append(value);
            if (i < keys.size() - 1) {
                dataToSign.append("&");
            }
        }

        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(checksumKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSha256.init(secretKey);
        byte[] hash = hmacSha256.doFinal(dataToSign.toString().getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        String generatedSignature = hexString.toString();

        return generatedSignature.equals(receivedSignature);
    }
}