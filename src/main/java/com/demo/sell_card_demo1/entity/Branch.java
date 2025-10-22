package com.demo.sell_card_demo1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "branches")
@Getter
@Setter
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Thường cần strategy nếu không dùng UUID() của DB
    @Column(name = "branch_id")
    private Long branchId;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    // Getters and Setters
}