package com.demo.sell_card_demo1.repository;

import com.demo.sell_card_demo1.entity.Order;
import com.demo.sell_card_demo1.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser_UserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Optional<Order> findByOrderIdAndUser_UserId(Long orderId, Long userId);

    // (MỚI) Hỗ trợ tìm kiếm Admin
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = 'COMPLETED' AND o.createdAt BETWEEN :from AND :to")
    Long sumRevenue(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    long countByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime from, LocalDateTime to);
}