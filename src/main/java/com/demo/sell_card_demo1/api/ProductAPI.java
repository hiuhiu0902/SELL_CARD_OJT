package com.demo.sell_card_demo1.api;

import com.demo.sell_card_demo1.dto.ProductResponse;
import com.demo.sell_card_demo1.dto.ProductVariantResponse;
import com.demo.sell_card_demo1.entity.Branch;
import com.demo.sell_card_demo1.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
// Không có @PreAuthorize vì đây là public
public class ProductAPI {
    @Autowired
    private ProductService productService;

    /**
     * SỬA: Thêm khả năng lọc (Filter).
     * Thay vì chỉ getAll, user muốn tìm theo Category, Branch, Keyword.
     */
    @GetMapping("/products/")
    public ResponseEntity<Page<ProductResponse>> getAllProduct(
            @RequestParam(required = false) String keyword,     // Tìm theo tên
            @RequestParam(required = false) String branchName,  // Lọc theo nhà mạng (Garena, Viettel...)
            @RequestParam(required = false) Long minPrice,      // Lọc giá
            @RequestParam(required = false) Long maxPrice,
            @ParameterObject Pageable pageable) {

        Page<ProductResponse> productResponse = productService.searchProductsPublic(keyword, branchName, minPrice, maxPrice, pageable);
        return ResponseEntity.ok().body(productResponse);
    }

    @GetMapping("/products/{productId}/variants")
    public ResponseEntity<List<ProductVariantResponse>> getProductVariant(@PathVariable Long productId) {
        List<ProductVariantResponse> responses = productService.getAllProductVariants(productId);
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        ProductResponse response = productService.getProductById(productId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/products/name/{productName}")
    public ResponseEntity<ProductResponse> getProductByName(@PathVariable String productName) {
        ProductResponse responses = productService.getProductByName(productName);
        return ResponseEntity.ok().body(responses);
    }
    @GetMapping("/products/branch/{branchId}")
    public ResponseEntity<Page<ProductResponse>> getProductsByBranch(
            @PathVariable Long branchId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getProductsByBranch(branchId, pageable));
    }

    @GetMapping("/branches/")
    public ResponseEntity getAllBranches() {
        List<Branch> branches = productService.getAllBranches();
        return ResponseEntity.ok().body(branches);
    }

}