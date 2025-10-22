package com.demo.sell_card_demo1.repository;

import com.demo.sell_card_demo1.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
