package com.demo.sell_card_demo1.repository;

import com.demo.sell_card_demo1.entity.Storage;
import com.demo.sell_card_demo1.enums.CardStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {
    long countByVariant_VariantIdAndStatus(Long variantId, CardStatus status);

    List<Storage> findByStatusAndVariant_VariantId(CardStatus status, Long variantId, Pageable pageable);

    List<Storage> findByOrderItem_ItemId(Long itemId);
}
