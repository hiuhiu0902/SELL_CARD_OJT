package com.demo.sell_card_demo1.dto;

import com.demo.sell_card_demo1.enums.CardStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CardDetailResponse {
    private Long storageId;
    private String serial;       // Nếu bạn có cột serial
    private String activateCode; // Mã nạp
    private LocalDateTime expirationDate;
    private CardStatus status;
    private String productName;
}