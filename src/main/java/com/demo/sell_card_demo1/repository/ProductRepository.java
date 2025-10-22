package com.demo.sell_card_demo1.repository;

import com.demo.sell_card_demo1.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    Boolean existsByName(String name);
    Product getProductByName(String name);
}
