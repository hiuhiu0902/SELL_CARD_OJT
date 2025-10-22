package com.demo.sell_card_demo1.api;

import com.demo.sell_card_demo1.dto.ProductResponse;
import com.demo.sell_card_demo1.dto.ProductVariantResponse;
import com.demo.sell_card_demo1.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@SecurityRequirement(
        name = "api"
)
public class ProductAPI {
    @Autowired
    private ProductService productService;
    @GetMapping
    public ResponseEntity getAllProduct(Pageable pageable){
        Page<ProductResponse> productResponse =  productService.getAllProducts(pageable);
        return ResponseEntity.ok().body(productResponse);
    }
    @GetMapping
    public ResponseEntity getProductVariant(@PathVariable Long productId){
        List<ProductVariantResponse> responses = productService.getAllProductVariants(productId);
        return ResponseEntity.ok().body(responses);
    }
    @GetMapping
    public ResponseEntity getProductById(@PathVariable Long productId){
        ProductResponse response = productService.getProductById(productId);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping
    public ResponseEntity getProductByName(@PathVariable String productName){
        ProductResponse responses = productService.getProductByName(productName);
        return ResponseEntity.ok().body(responses);
    }
}
