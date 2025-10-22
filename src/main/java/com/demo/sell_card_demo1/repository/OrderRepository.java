package com.demo.sell_card_demo1.repository;

import com.demo.sell_card_demo1.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Optional<Order> findByIdAndUser_Id(Long orderId, Long userId);
}
