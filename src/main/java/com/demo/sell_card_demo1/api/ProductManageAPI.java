package com.demo.sell_card_demo1.api;

import com.demo.sell_card_demo1.dto.*;
import com.demo.sell_card_demo1.entity.Product;
import com.demo.sell_card_demo1.entity.ProductVariant;
import com.demo.sell_card_demo1.repository.ProductRepository;
import com.demo.sell_card_demo1.repository.ProductVariantsRepository;
import com.demo.sell_card_demo1.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin/products") // Thay đổi base path cho nhất quán
@RestController
@SecurityRequirement(name = "api")
@PreAuthorize("hasRole('ADMIN')") // Áp dụng quyền ADMIN cho cả class
public class ProductManageAPI {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest request){
        Product newProduct = productService.createProduct(request);
        return ResponseEntity.ok(newProduct);
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(@ParameterObject Pageable pageable){
        Page<ProductResponse> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{productId}")
    public ResponseEntity getProductById(@PathVariable Long productId) {
        ProductResponse product = productService.getProductById(productId);
        return ResponseEntity.ok().body(product);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody UpdateProductRequest request) {
        Product updatedProduct = productService.updateProduct(productId, request);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build(); // Trả về 204 No Content là chuẩn REST
    }

    @PostMapping("/{productId}/variants")
    public ResponseEntity<ProductVariant> addVariant(@PathVariable Long productId, @RequestBody AddVariantRequest request){
        ProductVariant newVariant = productService.addProductVariant(productId, request);
        return ResponseEntity.ok(newVariant);
    }

    @GetMapping("/{productId}/variants")
    public ResponseEntity<List<ProductVariantResponse>> getAllProductVariants(@PathVariable Long productId){
        List<ProductVariantResponse> variants = productService.getAllProductVariants(productId);
        return ResponseEntity.ok(variants);
    }

    @PutMapping("/variants/{variantId}")
    public ResponseEntity<ProductVariant> updateVariant(@PathVariable Long variantId, @RequestBody UpdateVariantRequest request) {
        ProductVariant updatedVariant = productService.updateProductVariant(variantId, request);
        return ResponseEntity.ok(updatedVariant);
    }

//    // MỚI: API Xóa biến thể
//    @DeleteMapping("/variants/{variantId}")
//    public ResponseEntity<Void> deleteVariant(@PathVariable Long variantId) {
//        productService.deleteProductVariant(variantId);
//        return ResponseEntity.noContent().build();
//    }

    @PostMapping("/variants/{variantId}/stock")
    public ResponseEntity<Void> addStock(@PathVariable Long variantId, @RequestBody AddStockRequest request){
        productService.addStockToVariant(variantId, request);
        return ResponseEntity.ok().build();
    }
}