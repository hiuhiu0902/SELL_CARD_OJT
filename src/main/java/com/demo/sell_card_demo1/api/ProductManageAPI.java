package com.demo.sell_card_demo1.api;

import com.demo.sell_card_demo1.dto.*;
import com.demo.sell_card_demo1.entity.Branch;
import com.demo.sell_card_demo1.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/admin/products")
@RestController
@SecurityRequirement(name = "api")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class ProductManageAPI {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId, @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(productId, request));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{productId}/variants")
    public ResponseEntity<ProductVariantResponse> addVariant(@PathVariable Long productId, @RequestBody AddVariantRequest request) {
        return ResponseEntity.ok(productService.addProductVariant(productId, request));
    }

    @GetMapping("/{productId}/variants")
    public ResponseEntity<List<ProductVariantResponse>> getAllProductVariants(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getAllProductVariants(productId));
    }

    @PutMapping("/variants/{variantId}")
    public ResponseEntity<ProductVariantResponse> updateVariant(@PathVariable Long variantId, @RequestBody UpdateVariantRequest request) {
        return ResponseEntity.ok(productService.updateProductVariant(variantId, request));
    }


    @PostMapping("/variants/{variantId}/stock")
    public ResponseEntity<String> addStock(@PathVariable Long variantId, @RequestBody AddStockRequest request) {
        productService.addStockToVariant(variantId, request);
        return ResponseEntity.ok("Added stock successfully");
    }

    @GetMapping("/inventory-status")
    public ResponseEntity<List<InventoryStatusResponse>> getInventoryStatus() {
        return ResponseEntity.ok(productService.getInventoryStatus());
    }

    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(productService.uploadProductImage(id, file));
    }

    @PutMapping("/branch/{id}")
    public ResponseEntity deleteBranch(@PathVariable Long id) {
        productService.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/branch")
    public ResponseEntity createBranch(@RequestBody String branchName) {
        Branch branch = productService.createBranch(branchName);
        return ResponseEntity.ok().body(branch);
    }
}