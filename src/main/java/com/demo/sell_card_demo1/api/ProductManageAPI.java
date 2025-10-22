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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/admin")
@RestController
public class ProductManageAPI {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductService productService;
    @PostMapping("/add-product")
    @SecurityRequirement(
            name = "api"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public Product addProduct(@RequestBody CreateProductRequest request){
        System.out.println("Add lan 0");
        return productService.createProduct(request);
    }

    @PostMapping("/products/{productId}/add-variant")
    @SecurityRequirement(
            name = "api"
    )
    public ResponseEntity addVariant(@PathVariable Long productId, @RequestBody AddVariantRequest request){
        productService.addProductVariant(productId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/products/{variantId}/add-stock")
    @SecurityRequirement(
            name = "api"
    )
    public ResponseEntity addStock(@PathVariable Long variantId, @RequestBody AddStockRequest request){
        productService.addStockToVariant(variantId, request);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/products")
    @SecurityRequirement(
            name = "api"
    )
    public ResponseEntity<?> getAllProduct(@ParameterObject Pageable pageable){
        Page<ProductResponse> products =  productService.getAllProducts(pageable);
        return ResponseEntity.ok().body(products);
    }
    @GetMapping("/{productId}/productVariant")
    @SecurityRequirement(
            name = "api"
    )
    public ResponseEntity<?> getAllProductVariant(@PathVariable Long productId){
        List<ProductVariantResponse> variants = productService.getAllProductVariants(productId);
        return ResponseEntity.ok().body(variants);
    }
}

