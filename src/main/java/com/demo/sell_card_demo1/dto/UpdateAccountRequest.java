package com.demo.sell_card_demo1.dto;

import lombok.Data;

@Data
public class UpdateAccountRequest {
    public String fullName;
    public String address;
    public String phone;
}
