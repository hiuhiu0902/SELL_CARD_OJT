package com.demo.sell_card_demo1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;


@Configuration
public class PayOSConfig {
    @Value("${payos.client-id}")
    public String clientId;
    @Value("${payos.api-key}")
    public String apiKey;
    @Value("${payos.checksum-key}")
    public String checksumKey;

    @Bean
    public PayOS payOS() {
        return new PayOS(this.clientId, this.apiKey, this.checksumKey);
    }
}
