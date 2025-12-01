package com.demo.sell_card_demo1.api;

import com.demo.sell_card_demo1.dto.*;
import com.demo.sell_card_demo1.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/products")
@RestController
@SecurityRequirement(name = "api")
@PreAuthorize("hasRole('ADMIN')")
public class ProductManageAPI {

    @Autowired
    private ProductService productService;

    // SỬA: Trả về ProductResponse
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        ProductResponse newProduct = productService.createProduct(request);
        return ResponseEntity.ok(newProduct);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(@ParameterObject Pageable pageable) {
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    // SỬA: Trả về ProductResponse
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        ProductResponse product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }

    // SỬA: Trả về ProductResponse
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId, @RequestBody UpdateProductRequest request) {
        ProductResponse updatedProduct = productService.updateProduct(productId, request);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    // SỬA: Trả về ProductVariantResponse
    @PostMapping("/{productId}/variants")
    public ResponseEntity<ProductVariantResponse> addVariant(@PathVariable Long productId, @RequestBody AddVariantRequest request) {
        ProductVariantResponse newVariant = productService.addProductVariant(productId, request);
        return ResponseEntity.ok(newVariant);
    }

    @GetMapping("/{productId}/variants")
    public ResponseEntity<List<ProductVariantResponse>> getAllProductVariants(@PathVariable Long productId) {
        List<ProductVariantResponse> variants = productService.getAllProductVariants(productId);
        return ResponseEntity.ok(variants);
    }

    // SỬA: Trả về ProductVariantResponse
    @PutMapping("/variants/{variantId}")
    public ResponseEntity<ProductVariantResponse> updateVariant(@PathVariable Long variantId, @RequestBody UpdateVariantRequest request) {
        ProductVariantResponse updatedVariant = productService.updateProductVariant(variantId, request);
        return ResponseEntity.ok(updatedVariant);
    }



    // ... updateProduct, deleteProduct, addVariant ...

    @PostMapping("/variants/{variantId}/stock")
    public ResponseEntity<String> addStock(@PathVariable Long variantId, @RequestBody AddStockRequest request) {
        productService.addStockToVariant(variantId, request);
        return ResponseEntity.ok("Added stock successfully");
    }

    // --- PHẦN CẦN THÊM MỚI ---

    /**
     * THÊM: Upload ảnh sản phẩm.
     * Flow: Admin up ảnh -> Server trả về URL -> Admin dùng URL đó gọi createProduct.
     */
//    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
//        // Bạn cần viết hàm upload file trong service (lưu local hoặc S3)
//        String imageUrl = productService.uploadProductImage(file);
//        return ResponseEntity.ok(imageUrl);
//    }

    /**
     * THÊM: Xem tồn kho (Quan trọng).
     * Admin cần biết gói Garena 50k còn bao nhiêu thẻ để nhập thêm.
     */
    @GetMapping("/inventory-status")
    public ResponseEntity<List<InventoryStatusResponse>> getInventoryStatus() {
        // Hàm này lấy thống kê: Tên SP | Mệnh giá | Số lượng còn
        return ResponseEntity.ok(productService.getInventoryStatus());
    }
}