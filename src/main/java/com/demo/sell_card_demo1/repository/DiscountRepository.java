package com.demo.sell_card_demo1.repository;

import com.demo.sell_card_demo1.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    Discount findByCode(String code);
}
