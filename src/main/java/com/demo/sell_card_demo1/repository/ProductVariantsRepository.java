package com.demo.sell_card_demo1.repository;

import com.demo.sell_card_demo1.entity.ProductVariant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductVariantsRepository extends JpaRepository<ProductVariant, Long> {
    //    List<ProductVariant> findAll(Pageable pageable);
    Page<ProductVariant> findAll(Pageable pageable);

    List<ProductVariant> findByProduct_ProductId(Long productId);
}
