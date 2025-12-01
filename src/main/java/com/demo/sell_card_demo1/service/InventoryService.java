package com.demo.sell_card_demo1.service;

import com.demo.sell_card_demo1.dto.CardDetailResponse;
import com.demo.sell_card_demo1.dto.InventoryStatusResponse;
import com.demo.sell_card_demo1.entity.Storage;
import com.demo.sell_card_demo1.enums.CardStatus;
import com.demo.sell_card_demo1.exception.BadRequestException;
import com.demo.sell_card_demo1.repository.StorageRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class InventoryService {

    @Autowired
    private StorageRepository storageRepository;

    // 1. Xem thống kê tồn kho
    public List<InventoryStatusResponse> getInventoryStatus() {
        return storageRepository.countInventoryByVariant();
    }

    // 2. Tìm kiếm thẻ chi tiết (Admin soi thẻ)
    public Page<CardDetailResponse> searchCards(Long variantId, CardStatus status, String codeKeyword, Pageable pageable) {
        Specification<Storage> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (variantId != null) {
                predicates.add(cb.equal(root.get("variant").get("variantId"), variantId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (codeKeyword != null && !codeKeyword.isEmpty()) {
                predicates.add(cb.like(root.get("activateCode"), "%" + codeKeyword + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Storage> storagePage = storageRepository.findAll(spec, pageable);
        return storagePage.map(this::convertToCardDetail);
    }

    // 3. Xóa thẻ (Chỉ xóa thẻ chưa bán hoặc thẻ lỗi)
    public void deleteCard(Long storageId) {
        Storage card = storageRepository.findById(storageId)
                .orElseThrow(() -> new BadRequestException("Card not found"));

        if (card.getStatus() == CardStatus.USED || card.getStatus() == CardStatus.PENDING_PAYMENT) {
            throw new BadRequestException("Cannot delete card that is SOLD or PENDING");
        }
        storageRepository.delete(card);
    }

    // Helper convert
    private CardDetailResponse convertToCardDetail(Storage storage) {
        CardDetailResponse dto = new CardDetailResponse();
        dto.setStorageId(storage.getStorageId());
        dto.setActivateCode(storage.getActivateCode());
        dto.setExpirationDate(storage.getExpirationDate());
        dto.setStatus(storage.getStatus());
        dto.setProductName(storage.getVariant().getProduct().getName() + " - " + storage.getVariant().getPrice());
        return dto;
    }
}