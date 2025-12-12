package com.demo.sell_card_demo1.dto;

import com.demo.sell_card_demo1.enums.Gender;
import com.demo.sell_card_demo1.enums.Role;
import lombok.Data;

@Data
public class RegisterRequest {

    private String username;
    private String password;
    private String email;
    private String fullName;
    private String address;
    private Integer age;
    private Gender gender;
    private String phoneNumber;
    private Role role;

}
