package com.demo.sell_card_demo1.dto;

import com.demo.sell_card_demo1.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountResponse {
    public String name;
    public String username;
    public String email;
    public String phone;
    public String avatar;
    public Role role;
    public String token;
    public String address;
    public String avatarUrl;
}
