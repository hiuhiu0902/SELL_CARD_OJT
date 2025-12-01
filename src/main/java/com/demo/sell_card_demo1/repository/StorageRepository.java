package com.demo.sell_card_demo1.repository;

import com.demo.sell_card_demo1.dto.InventoryStatusResponse;
import com.demo.sell_card_demo1.entity.Storage;
import com.demo.sell_card_demo1.enums.CardStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    Page<Storage> findAll(Specification<Storage> spec, Pageable pageable);

    List<Storage> findByOrderItem_ItemId(Long itemId);

    long countByVariant_VariantIdAndStatus(Long variantId, CardStatus status);
    List<Storage> findByStatusAndVariant_VariantId(CardStatus status, Long variantId, Pageable pageable);

    // (MỚI) Thống kê tồn kho: Group by Product Variant
    @Query("SELECT new com.demo.sell_card_demo1.dto.InventoryStatusResponse(" +
            "v.variantId, p.name, v.price, COUNT(s)) " +
            "FROM Storage s " +
            "JOIN s.variant v " +
            "JOIN v.product p " +
            "WHERE s.status = 'UNUSED' " +
            "GROUP BY v.variantId, p.name, v.price")
    List<InventoryStatusResponse> countInventoryByVariant();
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Storage s WHERE s.status = :status AND s.variant.variantId = :variantId")
    List<Storage> findAndLockCards(@Param("status") CardStatus status,
                                   @Param("variantId") Long variantId,
                                   Pageable pageable);
}