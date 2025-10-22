package com.demo.sell_card_demo1.dto;

import lombok.Data;

@Data
public class BranchResponse {
    public Long id;
    public String name;

    public BranchResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
