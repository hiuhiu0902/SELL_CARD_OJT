package com.demo.sell_card_demo1.entity;

import com.demo.sell_card_demo1.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;
    ;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();
    private LocalDateTime activation_date;
    private Double total_price;
    @Column(name = "status", nullable = false)
    private TransactionStatus transactionStatus;
    private LocalDateTime paidAt;
    private LocalDateTime createAt;
    private String paymentCode;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
    }
}
