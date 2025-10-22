package com.demo.sell_card_demo1.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "discounts")
public class Discount {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dis_id;

    private String code;

    @Column(name = "max_usage", nullable = false)
    private Long maxUsage; // Đã đổi tên từ 'limit'

}