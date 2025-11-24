package com.demo.sell_card_demo1.repository;

import com.demo.sell_card_demo1.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser_UserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Optional<Order> findByOrderIdAndUser_UserId(Long orderId, Long userId);
}
