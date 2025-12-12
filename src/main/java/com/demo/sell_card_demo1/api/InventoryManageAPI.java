package com.demo.sell_card_demo1.api;

import com.demo.sell_card_demo1.dto.CardDetailResponse;
import com.demo.sell_card_demo1.dto.InventoryStatusResponse;
import com.demo.sell_card_demo1.enums.CardStatus;
import com.demo.sell_card_demo1.service.InventoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/inventory")
@SecurityRequirement(name = "api")
@PreAuthorize("hasRole('ADMIN')")
public class InventoryManageAPI {

    @Autowired
    private InventoryService inventoryService;

    // Xem tổng quan kho
    @GetMapping("/status")
    public ResponseEntity<List<InventoryStatusResponse>> getInventoryStatus() {
        return ResponseEntity.ok(inventoryService.getInventoryStatus());
    }

    // Tìm kiếm mã thẻ chi tiết
    @GetMapping("/cards")
    public ResponseEntity<Page<CardDetailResponse>> searchCards(
            @RequestParam(required = false) Long variantId,
            @RequestParam(required = false) CardStatus status,
            @RequestParam(required = false) String codeKeyword,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(inventoryService.searchCards(variantId, status, codeKeyword, pageable));
    }

    // Xóa thẻ hỏng/nhập sai
    @DeleteMapping("/cards/{storageId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long storageId) {
        inventoryService.deleteCard(storageId);
        return ResponseEntity.noContent().build();
    }
}