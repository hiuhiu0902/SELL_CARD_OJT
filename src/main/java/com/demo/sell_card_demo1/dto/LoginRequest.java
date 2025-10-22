package com.demo.sell_card_demo1.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
    public String username;
    public String password;
}
