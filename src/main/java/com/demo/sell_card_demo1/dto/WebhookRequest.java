package com.demo.sell_card_demo1.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class WebhookRequest {
    private JsonNode data;
    private String signature;
}
