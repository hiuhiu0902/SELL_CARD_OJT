package com.demo.sell_card_demo1.repository;

import com.demo.sell_card_demo1.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // <-- QUAN TRỌNG: Import cái này
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    boolean existsByName(String name);

    Optional<Product> findByName(String name);

    Product getProductByName(String name);

    Page<Product> findByBranch_BranchId(Long branchId, Pageable pageable);
}